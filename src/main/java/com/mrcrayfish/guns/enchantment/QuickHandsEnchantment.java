package com.mrcrayfish.guns.enchantment;


/**
 * Author: MrCrayfish
 */
public class QuickHandsEnchantment extends GunEnchantment
{
    public QuickHandsEnchantment()
    {
        super(2, Type.AMMO);
    }

    @Override
    public int getMaxLevel()
    {
        return 2;
    }

    @Override
    public int getMinCost(int level)
    {
        return 1 + 10 * (level - 1);
    }

    @Override
    public int getMaxCost(int level)
    {
        /* Old code used super.getMinCost(level) which resolved to the vanilla base formula 1 + (level - 1) * 10 */
        return 1 + (level - 1) * 10 + 50;
    }
}
