package com.mrcrayfish.guns.world;

import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Author: MrCrayfish
 */
public class ProjectileExplosion extends Explosion
{
    private static final ExplosionDamageCalculator DEFAULT_CONTEXT = new ExplosionDamageCalculator();

    private final Level world;
    private final double x;
    private final double y;
    private final double z;
    private final float size;
    private final Entity exploder;
    private final ExplosionDamageCalculator context;
    private final DamageSource damageSource;

    public ProjectileExplosion(Level world, Entity exploder, @Nullable DamageSource source, @Nullable ExplosionDamageCalculator context, double x, double y, double z, float size, boolean causesFire, BlockInteraction mode)
    {
        super(world, exploder, source, context, x, y, z, size, causesFire, mode, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE);
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = size;
        this.exploder = exploder;
        this.context = context == null ? DEFAULT_CONTEXT : context;
        this.damageSource = source != null ? source : world.damageSources().explosion(exploder, exploder);
    }

    @Override
    public void explode()
    {
        Set<BlockPos> set = Sets.newHashSet();
        for(int x = 0; x < 16; x++)
        {
            for(int y = 0; y < 16; y++)
            {
                for(int z = 0; z < 16; z++)
                {
                    if(x == 0 || x == 15 || y == 0 || y == 15 || z == 0 || z == 15)
                    {
                        double d0 = (double) ((float) x / 15.0F * 2.0F - 1.0F);
                        double d1 = (double) ((float) y / 15.0F * 2.0F - 1.0F);
                        double d2 = (double) ((float) z / 15.0F * 2.0F - 1.0F);
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 = d0 / d3;
                        d1 = d1 / d3;
                        d2 = d2 / d3;
                        float f = this.size * (0.7F + this.world.random.nextFloat() * 0.6F);
                        double blockX = this.x;
                        double blockY = this.y;
                        double blockZ = this.z;

                        for(; f > 0.0F; f -= 0.225F)
                        {
                            BlockPos pos = BlockPos.containing(blockX, blockY, blockZ);
                            BlockState blockState = this.world.getBlockState(pos);
                            FluidState fluidState = this.world.getFluidState(pos);
                            Optional<Float> optional = this.context.getBlockExplosionResistance(this, this.world, pos, blockState, fluidState);
                            if(optional.isPresent())
                            {
                                f -= (optional.get() + 0.3F) * 0.3F;
                            }

                            if(f > 0.0F && this.context.shouldBlockExplode(this, this.world, pos, blockState, f))
                            {
                                set.add(pos);
                            }

                            blockX += d0 * (double) 0.3F;
                            blockY += d1 * (double) 0.3F;
                            blockZ += d2 * (double) 0.3F;
                        }
                    }
                }
            }
        }

        this.getToBlow().addAll(set);

        float radius = this.size * 2.0F;
        int minX = Mth.floor(this.x - (double) radius - 1.0D);
        int maxX = Mth.floor(this.x + (double) radius + 1.0D);
        int minY = Mth.floor(this.y - (double) radius - 1.0D);
        int maxY = Mth.floor(this.y + (double) radius + 1.0D);
        int minZ = Mth.floor(this.z - (double) radius - 1.0D);
        int maxZ = Mth.floor(this.z + (double) radius + 1.0D);

        List<Entity> entities = this.world.getEntities(this.exploder, new AABB((double) minX, (double) minY, (double) minZ, (double) maxX, (double) maxY, (double) maxZ));

        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.level.ExplosionEvent.Detonate(this.world, this, entities));

        Vec3 explosionPos = new Vec3(this.x, this.y, this.z);
        for(Entity entity : entities)
        {
            if(entity.ignoreExplosion(this))
                continue;

            double strength = Math.sqrt(entity.distanceToSqr(explosionPos)) / radius;
            if(strength > 1.0D)
                continue;

            double deltaX = entity.getX() - this.x;
            double deltaY = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - this.y;
            double deltaZ = entity.getZ() - this.z;
            double distanceToExplosion = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

            if(distanceToExplosion != 0.0D)
            {
                deltaX /= distanceToExplosion;
                deltaY /= distanceToExplosion;
                deltaZ /= distanceToExplosion;
            }
            else
            {
                // Fixes an issue where explosion exactly on the player would cause no damage
                deltaX = 0.0;
                deltaY = 1.0;
                deltaZ = 0.0;
            }

            double blockDensity = (double) getSeenPercent(explosionPos, entity);
            double damage = (1.0D - strength) * blockDensity;
            entity.hurt(this.damageSource, (float) ((int) ((damage * damage + damage) / 2.0D * 7.0D * (double) radius + 1.0D)));

            double blastDamage = damage;
            if(entity instanceof LivingEntity living)
            {
                /* 1.20.1 used ProtectionEnchantment.getExplosionKnockbackAfterDampener which was removed in 1.21.
                 * Reimplemented per vanilla behaviour: reduces knockback by 15% per Blast Protection level (stacking). */
                int blastProtLevel = 0;
                for(ItemStack armor : living.getArmorSlots())
                {
                    blastProtLevel += EnchantmentHelper.getItemEnchantmentLevel(
                            world.registryAccess().holderOrThrow(Enchantments.BLAST_PROTECTION), armor);
                }
                if(blastProtLevel > 0)
                {
                    blastDamage *= Mth.clamp(1.0D - blastProtLevel * 0.15D, 0.0D, 1.0D);
                }
            }
            entity.setDeltaMovement(entity.getDeltaMovement().add(deltaX * blastDamage, deltaY * blastDamage, deltaZ * blastDamage));

            if(entity instanceof Player player)
            {
                if(!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying))
                {
                    this.getHitPlayers().put(player, new Vec3(deltaX * damage, deltaY * damage, deltaZ * damage));
                }
            }
        }
    }
}
