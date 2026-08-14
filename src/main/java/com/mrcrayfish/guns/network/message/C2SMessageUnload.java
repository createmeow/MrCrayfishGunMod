package com.mrcrayfish.guns.network.message;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.guns.common.network.ServerPlayHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;


import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class C2SMessageUnload
{
    public static void encode(C2SMessageUnload message, FriendlyByteBuf buffer) {}

    public static C2SMessageUnload decode(FriendlyByteBuf buffer)
    {
        return new C2SMessageUnload();
    }

    public static void handle(C2SMessageUnload message, MessageContext context)
    {
        context.execute(() ->
        {
            ServerPlayer player = (ServerPlayer) context.getPlayer().orElse(null);
            if(player != null && !player.isSpectator())
            {
                ServerPlayHandler.handleUnload(player);
            }
        });
        context.setHandled(true);
    }
}