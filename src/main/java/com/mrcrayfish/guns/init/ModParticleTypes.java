package com.mrcrayfish.guns.init;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.guns.Reference;
import com.mrcrayfish.guns.particles.BulletHoleData;
import com.mrcrayfish.guns.particles.TrailData;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Author: MrCrayfish
 */
public class ModParticleTypes
{
    public static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(Registries.PARTICLE_TYPE, Reference.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, ParticleType<BulletHoleData>> BULLET_HOLE = REGISTER.register("bullet_hole", () -> new ParticleType<BulletHoleData>(false)
    {
        @Override
        public MapCodec<BulletHoleData> codec()
        {
            return BulletHoleData.CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, BulletHoleData> streamCodec()
        {
            return BulletHoleData.STREAM_CODEC;
        }
    });

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOD = REGISTER.register("blood", () -> new SimpleParticleType(true));

    public static final DeferredHolder<ParticleType<?>, ParticleType<TrailData>> TRAIL = REGISTER.register("trail", () -> new ParticleType<TrailData>(false)
    {
        @Override
        public MapCodec<TrailData> codec()
        {
            return TrailData.CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, TrailData> streamCodec()
        {
            return TrailData.STREAM_CODEC;
        }
    });
}
