package com.mrcrayfish.guns.enchantment;


/**
 * Author: MrCrayfish
 */
public class FireStarterEnchantment extends GunEnchantment
{
    public FireStarterEnchantment()
    {
        super(1, Type.PROJECTILE);
    }

    @Override
    public int getMinCost(int level)
    {
        return 15;
    }

    @Override
    public int getMaxCost(int level)
    {
        return this.getMinCost(level) + 30;
    }
}
