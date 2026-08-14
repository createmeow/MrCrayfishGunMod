package com.mrcrayfish.guns.enchantment;


/**
 * Author: MrCrayfish
 */
public class AcceleratorEnchantment extends GunEnchantment
{
    public AcceleratorEnchantment()
    {
        super(2, Type.PROJECTILE);
    }

    @Override
    public int getMaxLevel()
    {
        return 2;
    }

    @Override
    public int getMinCost(int level)
    {
        return 10 + (level - 1) * 10;
    }

    @Override
    public int getMaxCost(int level)
    {
        return this.getMinCost(level) + 20;
    }
}
