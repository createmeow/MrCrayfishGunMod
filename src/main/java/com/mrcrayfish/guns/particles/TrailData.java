package com.mrcrayfish.guns.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.guns.init.ModParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * Author: MrCrayfish
 */
public class TrailData implements ParticleOptions
{
    public static final MapCodec<TrailData> CODEC = RecordCodecBuilder.mapCodec((builder) -> builder
            .group(Codec.BOOL.fieldOf("enchanted").forGetter((data) -> data.enchanted))
            .apply(builder, TrailData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TrailData> STREAM_CODEC = StreamCodec.ofMember(
            (data, buffer) -> buffer.writeBoolean(data.enchanted),
            buffer -> new TrailData(buffer.readBoolean()));

    private boolean enchanted;

    public TrailData(boolean enchanted)
    {
        this.enchanted = enchanted;
    }

    public boolean isEnchanted()
    {
        return this.enchanted;
    }

    @Override
    public ParticleType<?> getType()
    {
        return ModParticleTypes.TRAIL.get();
    }
}
