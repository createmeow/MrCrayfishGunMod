package com.mrcrayfish.guns.enchantment;


/**
 * Author: MrCrayfish
 */
public class PuncturingEnchantment extends GunEnchantment
{
    public PuncturingEnchantment()
    {
        super(2, Type.PROJECTILE);
    }

    @Override
    public int getMaxLevel()
    {
        return 4;
    }

    @Override
    public int getMinCost(int level)
    {
        return 1 + (level - 1) * 10;
    }

    @Override
    public int getMaxCost(int level)
    {
        return this.getMinCost(level) + 10;
    }
}
