package com.mrcrayfish.guns.enchantment;


/**
 * Author: MrCrayfish
 */
public class ReclaimedEnchantment extends GunEnchantment
{
    public ReclaimedEnchantment()
    {
        super(1, Type.AMMO);
    }

    @Override
    public int getMaxLevel()
    {
        return 2;
    }

    @Override
    public int getMinCost(int level)
    {
        return 15 + (level - 1) * 10;
    }

    @Override
    public int getMaxCost(int level)
    {
        return this.getMinCost(level) + 10;
    }
}
