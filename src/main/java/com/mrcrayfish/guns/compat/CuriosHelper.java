package com.mrcrayfish.guns.compat;

import net.minecraft.world.entity.player.Player;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class CuriosHelper {
    public static void runOnCurios(Player player, Consumer<? super ICuriosItemHandler> consumer) {
        CuriosApi.getCuriosInventory(player).ifPresent(consumer);
    }
}
