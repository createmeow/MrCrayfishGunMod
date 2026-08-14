package com.mrcrayfish.guns.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.guns.init.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * Author: MrCrayfish
 */
public class BulletHoleData implements ParticleOptions
{
    public static final MapCodec<BulletHoleData> CODEC = RecordCodecBuilder.mapCodec((builder) -> builder.group(
            Codec.INT.fieldOf("dir").forGetter((data) -> data.direction.ordinal()),
            Codec.LONG.fieldOf("pos").forGetter((data) -> data.pos.asLong())
    ).apply(builder, BulletHoleData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BulletHoleData> STREAM_CODEC = StreamCodec.ofMember(
            (data, buffer) ->
            {
                buffer.writeEnum(data.direction);
                buffer.writeBlockPos(data.pos);
            },
            buffer -> new BulletHoleData(buffer.readEnum(Direction.class), buffer.readBlockPos())
    );

    private final Direction direction;
    private final BlockPos pos;

    public BulletHoleData(int dir, long pos)
    {
        this.direction = Direction.values()[dir];
        this.pos = BlockPos.of(pos);
    }

    public BulletHoleData(Direction dir, BlockPos pos)
    {
        this.direction = dir;
        this.pos = pos;
    }

    public Direction getDirection()
    {
        return this.direction;
    }

    public BlockPos getPos()
    {
        return this.pos;
    }

    @Override
    public ParticleType<?> getType()
    {
        return ModParticleTypes.BULLET_HOLE.get();
    }
}
