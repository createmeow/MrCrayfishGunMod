package com.mrcrayfish.guns.network.message;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.guns.common.network.ServerPlayHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

/**
 * Author: MrCrayfish
 */
public class C2SMessageAttachments
{
    public C2SMessageAttachments() {}

    public static void encode(C2SMessageAttachments message, FriendlyByteBuf buffer) {}

    public static C2SMessageAttachments decode(FriendlyByteBuf buffer)
    {
        return new C2SMessageAttachments();
    }

    public static void handle(C2SMessageAttachments message, MessageContext context)
    {
        context.execute(() ->
        {
            ServerPlayer player = (ServerPlayer) context.getPlayer().orElse(null);
            if(player != null)
            {
                ServerPlayHandler.handleAttachments(player);
            }
        });
        context.setHandled(true);
    }
}