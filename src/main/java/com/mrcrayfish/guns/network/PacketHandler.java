package com.mrcrayfish.guns.network;

import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.guns.Reference;
import com.mrcrayfish.guns.network.message.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamMemberEncoder;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;

public class PacketHandler
{
    private static FrameworkNetwork playChannel;

    public static void init()
    {
        playChannel = FrameworkAPI.createNetworkBuilder(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "play"), 1)
                .registerPlayMessage("aim", C2SMessageAim.class, codec(C2SMessageAim::encode, C2SMessageAim::decode), C2SMessageAim::handle, PacketFlow.SERVERBOUND)
                .registerPlayMessage("reload", C2SMessageReload.class, codec(C2SMessageReload::encode, C2SMessageReload::decode), C2SMessageReload::handle, PacketFlow.SERVERBOUND)
                .registerPlayMessage("shoot", C2SMessageShoot.class, codec(C2SMessageShoot::encode, C2SMessageShoot::decode), C2SMessageShoot::handle, PacketFlow.SERVERBOUND)
                .registerPlayMessage("unload", C2SMessageUnload.class, codec(C2SMessageUnload::encode, C2SMessageUnload::decode), C2SMessageUnload::handle, PacketFlow.SERVERBOUND)
                .registerPlayMessage("stun_grenade", S2CMessageStunGrenade.class, codec(S2CMessageStunGrenade::encode, S2CMessageStunGrenade::decode), S2CMessageStunGrenade::handle, PacketFlow.CLIENTBOUND)
                .registerPlayMessage("craft", C2SMessageCraft.class, codec(C2SMessageCraft::encode, C2SMessageCraft::decode), C2SMessageCraft::handle, PacketFlow.SERVERBOUND)
                .registerPlayMessage("bullet_trail", S2CMessageBulletTrail.class, StreamCodec.ofMember(S2CMessageBulletTrail::encode, S2CMessageBulletTrail::decode), S2CMessageBulletTrail::handle, PacketFlow.CLIENTBOUND)
                .registerPlayMessage("attachments", C2SMessageAttachments.class, codec(C2SMessageAttachments::encode, C2SMessageAttachments::decode), C2SMessageAttachments::handle, PacketFlow.SERVERBOUND)
                .registerPlayMessage("update_guns", S2CMessageUpdateGuns.class, codec(S2CMessageUpdateGuns::encode, S2CMessageUpdateGuns::decode), S2CMessageUpdateGuns::handle, PacketFlow.CLIENTBOUND)
                .registerPlayMessage("blood", S2CMessageBlood.class, codec(S2CMessageBlood::encode, S2CMessageBlood::decode), S2CMessageBlood::handle, PacketFlow.CLIENTBOUND)
                .registerPlayMessage("shooting", C2SMessageShooting.class, codec(C2SMessageShooting::encode, C2SMessageShooting::decode), C2SMessageShooting::handle, PacketFlow.SERVERBOUND)
                .registerPlayMessage("gun_sound", S2CMessageGunSound.class, codec(S2CMessageGunSound::encode, S2CMessageGunSound::decode), S2CMessageGunSound::handle, PacketFlow.CLIENTBOUND)
                .registerPlayMessage("projectile_hit_block", S2CMessageProjectileHitBlock.class, codec(S2CMessageProjectileHitBlock::encode, S2CMessageProjectileHitBlock::decode), S2CMessageProjectileHitBlock::handle, PacketFlow.CLIENTBOUND)
                .registerPlayMessage("projectile_hit_entity", S2CMessageProjectileHitEntity.class, codec(S2CMessageProjectileHitEntity::encode, S2CMessageProjectileHitEntity::decode), S2CMessageProjectileHitEntity::handle, PacketFlow.CLIENTBOUND)
                .registerPlayMessage("remove_projectile", S2CMessageRemoveProjectile.class, codec(S2CMessageRemoveProjectile::encode, S2CMessageRemoveProjectile::decode), S2CMessageRemoveProjectile::handle, PacketFlow.CLIENTBOUND)
                .build();
    }

    /**
     * Adapts the {@link FriendlyByteBuf} based codec used by messages to the {@link RegistryFriendlyByteBuf}
     * codec expected by the Framework network.
     */
    private static <T> StreamCodec<RegistryFriendlyByteBuf, T> codec(StreamMemberEncoder<FriendlyByteBuf, T> encoder, StreamDecoder<FriendlyByteBuf, T> decoder)
    {
        return StreamCodec.ofMember(encoder, decoder).cast();
    }

    /**
     * Gets the play network channel for MrCrayfish's Gun Mod
     */
    public static FrameworkNetwork getPlayChannel()
    {
        return playChannel;
    }
}
