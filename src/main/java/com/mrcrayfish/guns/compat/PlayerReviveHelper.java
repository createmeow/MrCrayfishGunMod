package com.mrcrayfish.guns.compat;

import com.mrcrayfish.guns.GunMod;
import net.minecraft.world.entity.player.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Author: MrCrayfish
 */
public class PlayerReviveHelper
{
    private static boolean disable = false;
    private static Method isBleeding;

    public static boolean isBleeding(Player player)
    {
        if(!GunMod.playerReviveLoaded || disable)
            return false;

        try
        {
            init();
            return (boolean) isBleeding.invoke(null, player);
        }
        catch(InvocationTargetException | IllegalAccessException e)
        {
            disable = true;
        }
        return false;
    }

    private static void init()
    {
        if(isBleeding == null)
        {
            try
            {
                Class<?> playerReviveServer = Class.forName("team.creative.playerrevive.server.PlayerReviveServer");
                isBleeding = playerReviveServer.getDeclaredMethod("isBleeding", Player.class);
            }
            catch(ClassNotFoundException | NoSuchMethodException ignored)
            {
                disable = true;
            }
        }
    }
}
