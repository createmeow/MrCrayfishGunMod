package com.mrcrayfish.guns.enchantment;

import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.function.IntUnaryOperator;

/**
 * Author: MrCrayfish
 */
public abstract class GunEnchantment
{
    private final int weight;
    private final Type type;

    protected GunEnchantment(int weight, Type type)
    {
        this.weight = weight;
        this.type = type;
    }

    public int getMaxLevel()
    {
        return 1;
    }

    public abstract int getMinCost(int level);

    public abstract int getMaxCost(int level);

    /**
     * Builds the enchantment for registration with the given supported items.
     */
    public Enchantment build(ResourceLocation id, HolderSet<Item> supportedItems)
    {
        Enchantment.EnchantmentDefinition definition = Enchantment.definition(
                supportedItems,
                this.weight,
                this.getMaxLevel(),
                new Enchantment.Cost(this.getMinCost(1), this.getPerLevelCost(this::getMinCost)),
                new Enchantment.Cost(this.getMaxCost(1), this.getPerLevelCost(this::getMaxCost)),
                1,
                EquipmentSlotGroup.MAINHAND);
        return new Enchantment(
                Component.translatable("enchantment." + id.getNamespace() + "." + id.getPath()),
                definition,
                HolderSet.direct(),
                DataComponentMap.EMPTY);
    }

    private int getPerLevelCost(IntUnaryOperator costFunction)
    {
        int base = costFunction.applyAsInt(1);
        int next = costFunction.applyAsInt(2);
        return Math.max(next - base, 0);
    }

    public enum Type
    {
        WEAPON, AMMO, PROJECTILE
    }
}
