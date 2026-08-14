package com.mrcrayfish.guns.common;

import com.google.gson.JsonDeserializer;
import com.mojang.serialization.JsonOps;
import com.mrcrayfish.guns.client.util.Easings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class JsonDeserializers
{
    public static final JsonDeserializer<ItemStack> ITEM_STACK = (json, typeOfT, context) -> ItemStack.CODEC.parse(JsonOps.INSTANCE, json).result().orElse(ItemStack.EMPTY);
    public static final JsonDeserializer<ResourceLocation> RESOURCE_LOCATION = (json, typeOfT, context) -> ResourceLocation.parse(json.getAsString());
    public static final JsonDeserializer<GripType> GRIP_TYPE = (json, typeOfT, context) -> GripType.getType(ResourceLocation.tryParse(json.getAsString()));
    public static final JsonDeserializer<Easings> EASING = (json, typeOfT, context) -> Easings.byName(json.getAsString());
}

