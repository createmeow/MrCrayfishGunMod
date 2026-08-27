package com.mrcrayfish.guns.enchantment;


/**
 * Author: MrCrayfish
 */
public class OverCapacityEnchantment extends GunEnchantment
{
    public OverCapacityEnchantment()
    {
        super(2, Type.WEAPON);
    }

    @Override
    public int getMaxLevel()
    {
        return 3;
    }

    @Override
    public int getMinCost(int level)
    {
        return 5 + (level - 1) * 10;
    }

    @Override
    public int getMaxCost(int level)
    {
        /* Old code used super.getMinCost(level) which resolved to the vanilla base formula 1 + (level - 1) * 10 */
        return 1 + (level - 1) * 10 + 50;
    }
}
