package com.mrcrayfish.guns.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

/**
 * Author: MrCrayfish
 */
public class WorkbenchIngredient
{
    private final Ingredient.Value itemList;
    private final int count;

    protected WorkbenchIngredient(Stream<? extends Ingredient.Value> itemList, int count)
    {
        this.itemList = itemList.findFirst().orElseThrow(() -> new JsonSyntaxException("Workbench ingredient must contain an item or tag"));
        this.count = count;
    }

    private WorkbenchIngredient(Ingredient.Value itemList, int count)
    {
        this.itemList = itemList;
        this.count = count;
    }

    public int getCount()
    {
        return this.count;
    }

    public boolean test(ItemStack stack)
    {
        if(stack == null || stack.isEmpty())
            return false;
        for(ItemStack value : this.itemList.getItems())
        {
            if(value.is(stack.getItem()))
                return true;
        }
        return false;
    }

    public ItemStack[] getItems()
    {
        return this.itemList.getItems().toArray(new ItemStack[0]);
    }

    public JsonElement toJson()
    {
        JsonObject object = new JsonObject();
        if(this.itemList instanceof Ingredient.ItemValue itemValue)
        {
            object.addProperty("item", BuiltInRegistries.ITEM.getKey(itemValue.item().getItem()).toString());
        }
        else if(this.itemList instanceof Ingredient.TagValue tagValue)
        {
            object.addProperty("tag", tagValue.tag().location().toString());
        }
        else if(this.itemList instanceof UnknownValue unknownValue)
        {
            object.addProperty("item", unknownValue.id.toString());
        }
        object.addProperty("count", this.count);
        return object;
    }

    public static WorkbenchIngredient fromJson(JsonObject object)
    {
        Ingredient.Value value;
        if(object.has("item"))
        {
            ResourceLocation id = ResourceLocation.parse(GsonHelper.getAsString(object, "item"));
            if(BuiltInRegistries.ITEM.containsKey(id))
            {
                value = new Ingredient.ItemValue(new ItemStack(BuiltInRegistries.ITEM.get(id)));
            }
            else
            {
                value = new UnknownValue(id);
            }
        }
        else if(object.has("tag"))
        {
            ResourceLocation id = ResourceLocation.parse(GsonHelper.getAsString(object, "tag"));
            value = new Ingredient.TagValue(TagKey.create(Registries.ITEM, id));
        }
        else
        {
            throw new JsonSyntaxException("Workbench ingredient must contain an item or tag");
        }
        int count = GsonHelper.getAsInt(object, "count", 1);
        return new WorkbenchIngredient(value, count);
    }

    public static WorkbenchIngredient of(ItemLike provider, int count)
    {
        return new WorkbenchIngredient(new Ingredient.ItemValue(new ItemStack(provider)), count);
    }

    public static WorkbenchIngredient of(ItemStack stack, int count)
    {
        return new WorkbenchIngredient(new Ingredient.ItemValue(stack), count);
    }

    public static WorkbenchIngredient of(TagKey<Item> tag, int count)
    {
        return new WorkbenchIngredient(new Ingredient.TagValue(tag), count);
    }

    public static WorkbenchIngredient of(ResourceLocation id, int count)
    {
        return new WorkbenchIngredient(new UnknownValue(id), count);
    }

    /**
     * Allows ability to define an ingredient from another mod without adding it as a dependency in
     * the development environment. Only use this for generating data.
     */
    @SuppressWarnings("ClassCanBeRecord")
    public static class UnknownValue implements Ingredient.Value
    {
        private final ResourceLocation id;

        public UnknownValue(ResourceLocation id)
        {
            this.id = id;
        }

        @Override
        public Collection<ItemStack> getItems()
        {
            return Collections.emptyList();
        }
    }
}

