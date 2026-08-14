package com.mrcrayfish.guns.crafting;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.stream.Stream;

/**
 * Author: MrCrayfish
 */
public class WorkbenchRecipeSerializer implements RecipeSerializer<WorkbenchRecipe>
{
    private static final ResourceLocation DEFAULT_ID = ResourceLocation.fromNamespaceAndPath("cgm", "workbench");

    private static final MapCodec<WorkbenchRecipe> CODEC = new MapCodec<WorkbenchRecipe>()
    {
        @Override
        public <T> DataResult<WorkbenchRecipe> decode(DynamicOps<T> ops, MapLike<T> input)
        {
            // 使用 MapLike.get() 直接获取 materials 和 result
            T materialsKey = ops.createString("materials");
            T resultKey = ops.createString("result");

            T materialsElement = input.get(materialsKey);
            if(materialsElement == null)
            {
                return DataResult.error(() -> "Missing materials entry");
            }
            T resultElement = input.get(resultKey);
            if(resultElement == null)
            {
                return DataResult.error(() -> "Missing result item entry");
            }

            // 将 materials 转为 JsonArray 后用 GsonHelper 解析
            JsonElement materialsJson = ops.convertTo(JsonOps.INSTANCE, materialsElement);
            ImmutableList.Builder<WorkbenchIngredient> builder = ImmutableList.builder();
            JsonArray materialsArray = GsonHelper.convertToJsonArray(materialsJson, "materials");
            for(int i = 0; i < materialsArray.size(); i++)
            {
                builder.add(WorkbenchIngredient.fromJson(materialsArray.get(i).getAsJsonObject()));
            }

            // Manually parse result to support both "item" (legacy) and "id" (1.21.1) keys,
            // since ItemStack.CODEC in 1.21.1 only accepts "id".
            JsonElement resultJson = ops.convertTo(JsonOps.INSTANCE, resultElement);
            if(!resultJson.isJsonObject())
            {
                return DataResult.error(() -> "Result must be a JSON object");
            }
            JsonObject resultObject = resultJson.getAsJsonObject();
            String itemId;
            if(resultObject.has("item"))
            {
                itemId = GsonHelper.getAsString(resultObject, "item");
            }
            else if(resultObject.has("id"))
            {
                itemId = GsonHelper.getAsString(resultObject, "id");
            }
            else
            {
                return DataResult.error(() -> "Missing result item/id entry");
            }
            ResourceLocation itemLoc = ResourceLocation.parse(itemId);
            if(!BuiltInRegistries.ITEM.containsKey(itemLoc))
            {
                return DataResult.error(() -> "Unknown item: " + itemId);
            }
            Item item = BuiltInRegistries.ITEM.get(itemLoc);
            int count = GsonHelper.getAsInt(resultObject, "count", 1);
            ItemStack resultItem = new ItemStack(item, count);
            // Use the result item's registry name as the recipe ID so each recipe is unique
            ResourceLocation recipeId = resultItem.isEmpty() ? DEFAULT_ID : BuiltInRegistries.ITEM.getKey(resultItem.getItem());
            return DataResult.success(new WorkbenchRecipe(recipeId, resultItem, builder.build()));
        }

        @Override
        public <T> RecordBuilder<T> encode(WorkbenchRecipe input, DynamicOps<T> ops, RecordBuilder<T> prefix)
        {
            JsonObject json = toJson(input);
            return prefix.add("materials", JsonOps.INSTANCE.convertTo(ops, json.get("materials")))
                    .add("result", JsonOps.INSTANCE.convertTo(ops, json.get("result")));
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops)
        {
            return Stream.of("materials", "result").map(ops::createString);
        }
    };

    private static final StreamCodec<RegistryFriendlyByteBuf, WorkbenchRecipe> STREAM_CODEC = StreamCodec.ofMember(
            WorkbenchRecipeSerializer::write,
            WorkbenchRecipeSerializer::read
    );

    public static JsonObject toJson(WorkbenchRecipe recipe)
    {
        JsonObject json = new JsonObject();
        JsonArray materials = new JsonArray();
        recipe.getMaterials().forEach(ingredient -> materials.add(ingredient.toJson()));
        json.add("materials", materials);

        JsonObject resultObject = new JsonObject();
        resultObject.addProperty("id", BuiltInRegistries.ITEM.getKey(recipe.getItem().getItem()).toString());
        int count = recipe.getItem().getCount();
        if(count > 1)
        {
            resultObject.addProperty("count", count);
        }
        json.add("result", resultObject);
        return json;
    }

    public static WorkbenchRecipe fromJson(ResourceLocation recipeId, JsonObject parent)
    {
        ImmutableList.Builder<WorkbenchIngredient> builder = ImmutableList.builder();
        JsonArray input = GsonHelper.getAsJsonArray(parent, "materials");
        for(int i = 0; i < input.size(); i++)
        {
            JsonObject object = input.get(i).getAsJsonObject();
            builder.add(WorkbenchIngredient.fromJson(object));
        }
        if(!parent.has("result"))
        {
            throw new JsonSyntaxException("Missing result item entry");
        }
        JsonObject resultObject = GsonHelper.getAsJsonObject(parent, "result");
        String itemId;
        if(resultObject.has("item"))
        {
            itemId = GsonHelper.getAsString(resultObject, "item");
        }
        else if(resultObject.has("id"))
        {
            itemId = GsonHelper.getAsString(resultObject, "id");
        }
        else
        {
            throw new JsonSyntaxException("Missing result item/id entry");
        }
        ResourceLocation itemLoc = ResourceLocation.parse(itemId);
        if(!BuiltInRegistries.ITEM.containsKey(itemLoc))
        {
            throw new JsonSyntaxException("Unknown item: " + itemId);
        }
        Item item = BuiltInRegistries.ITEM.get(itemLoc);
        int count = GsonHelper.getAsInt(resultObject, "count", 1);
        ItemStack resultItem = new ItemStack(item, count);
        return new WorkbenchRecipe(recipeId, resultItem, builder.build());
    }

    private static WorkbenchRecipe read(RegistryFriendlyByteBuf buffer)
    {
        ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
        ResourceLocation recipeId = result.isEmpty() ? DEFAULT_ID : BuiltInRegistries.ITEM.getKey(result.getItem());
        ImmutableList.Builder<WorkbenchIngredient> builder = ImmutableList.builder();
        int size = buffer.readVarInt();
        for(int i = 0; i < size; i++)
        {
            int itemCount = buffer.readVarInt();
            int count = buffer.readVarInt();
            Stream<Ingredient.ItemValue> values = Stream.generate(() -> new Ingredient.ItemValue(ItemStack.STREAM_CODEC.decode(buffer))).limit(itemCount);
            builder.add(new WorkbenchIngredient(values, count));
        }
        return new WorkbenchRecipe(recipeId, result, builder.build());
    }

    private static void write(WorkbenchRecipe recipe, RegistryFriendlyByteBuf buffer)
    {
        ItemStack.STREAM_CODEC.encode(buffer, recipe.getItem());
        buffer.writeVarInt(recipe.getMaterials().size());
        for(WorkbenchIngredient ingredient : recipe.getMaterials())
        {
            buffer.writeVarInt(ingredient.getItems().length);
            buffer.writeVarInt(ingredient.getCount());
            for(ItemStack stack : ingredient.getItems())
            {
                ItemStack.STREAM_CODEC.encode(buffer, stack);
            }
        }
    }

    @Override
    public MapCodec<WorkbenchRecipe> codec()
    {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, WorkbenchRecipe> streamCodec()
    {
        return STREAM_CODEC;
    }
}
