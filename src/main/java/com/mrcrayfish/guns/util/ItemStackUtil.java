package com.mrcrayfish.guns.util;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * Compatibility helpers for the legacy ItemStack NBT tag API which was replaced
 * by data components in 1.21. Note that components are immutable, so any
 * modification made to the tag returned by {@link #getOrCreateTag} must be
 * written back using {@link #setTag}.
 *
 * Author: MrCrayfish
 */
public final class ItemStackUtil
{
    private ItemStackUtil() {}

    public static CompoundTag getTag(ItemStack stack)
    {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null ? data.copyTag() : null;
    }

    public static boolean hasTag(ItemStack stack)
    {
        return stack.get(DataComponents.CUSTOM_DATA) != null;
    }

    public static CompoundTag getOrCreateTag(ItemStack stack)
    {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    public static void setTag(ItemStack stack, CompoundTag tag)
    {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static ItemStack parse(CompoundTag tag)
    {
        return ItemStack.parseOptional(RegistryAccess.EMPTY, tag);
    }
}
