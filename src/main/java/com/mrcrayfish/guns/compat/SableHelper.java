package com.mrcrayfish.guns.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.fml.ModList;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Integration with the Sable (sub-levels) library mod. Allows gun projectiles to collide
 * with blocks living inside sub-levels instead of passing straight through their plots.
 * <p>
 * This class is the safe entry point — it contains no direct references to Sable classes.
 * When Sable is loaded, calls are delegated to {@link SableHelperImpl} which is only
 * class-loaded at that point, preventing NoClassDefFoundError when Sable is absent.
 */
public class SableHelper {

    public static boolean isSableLoaded() {
        return ModList.get() != null && ModList.get().isLoaded("sable");
    }

    /**
     * Ray traces blocks in the given level, including blocks stored inside any intersecting
     * sub-levels. If Sable is not loaded, this behaves exactly like the original custom
     * implementation found in {@code ProjectileEntity}.
     */
    public static BlockHitResult rayTraceBlocks(Level level, Vec3 start, Vec3 end, ClipContext.Block blockMode, ClipContext.Fluid fluidMode, CollisionContext collisionContext, Predicate<BlockState> ignorePredicate) {
        ClipContext context = new ClipContext(start, end, blockMode, fluidMode, collisionContext);
        BlockHitResult result = performRayTrace(level, context, ignorePredicate);
        if(!isSableLoaded()) {
            return result;
        }
        return SableHelperImpl.rayTraceBlocks(level, start, end, blockMode, fluidMode, collisionContext, ignorePredicate);
    }

    /**
     * Checks if the given position (in plot space) is inside the plot grid of any sub-level.
     */
    public static boolean isInPlotGrid(Level level, BlockPos pos) {
        return isSableLoaded() && SableHelperImpl.isInPlotGrid(level, pos);
    }

    /**
     * Gets the actual block state stored at the given plot-space position, if it belongs to a sub-level.
     */
    @Nullable
    public static BlockState getSubLevelState(Level level, BlockPos plotPos) {
        if(!isSableLoaded()) {
            return null;
        }
        return SableHelperImpl.getSubLevelState(level, plotPos);
    }

    /**
     * Gets the level of the sub-level that contains the given plot-space position, or null if none does.
     */
    @Nullable
    public static Level getSubLevelLevel(Level level, BlockPos plotPos) {
        if(!isSableLoaded()) {
            return null;
        }
        return SableHelperImpl.getSubLevelLevel(level, plotPos);
    }

    /**
     * Projects a plot-space position out of its sub-level into global space. If the position is
     * not inside a sub-level, the position is returned unchanged.
     */
    public static Vec3 projectOutOfSubLevel(Level level, Vec3 plotPos) {
        if(!isSableLoaded()) {
            return plotPos;
        }
        return SableHelperImpl.projectOutOfSubLevel(level, plotPos);
    }

    /**
     * A custom implementation of ray tracing that allows you to pass a predicate to ignore certain
     * blocks when checking for collisions. Ported from {@code ProjectileEntity}.
     */
    static BlockHitResult performRayTrace(Level world, ClipContext context, Predicate<BlockState> ignorePredicate) {
        return performRayTrace(context, (rayTraceContext, blockPos) -> {
            BlockState blockState = world.getBlockState(blockPos);
            if(ignorePredicate.test(blockState)) return null;
            FluidState fluidState = world.getFluidState(blockPos);
            Vec3 startVec = rayTraceContext.getFrom();
            Vec3 endVec = rayTraceContext.getTo();
            var blockShape = rayTraceContext.getBlockShape(blockState, world, blockPos);
            BlockHitResult blockResult = world.clipWithInteractionOverride(startVec, endVec, blockPos, blockShape, blockState);
            var fluidShape = rayTraceContext.getFluidShape(fluidState, world, blockPos);
            BlockHitResult fluidResult = fluidShape.clip(startVec, endVec, blockPos);
            double blockDistance = blockResult == null ? Double.MAX_VALUE : rayTraceContext.getFrom().distanceToSqr(blockResult.getLocation());
            double fluidDistance = fluidResult == null ? Double.MAX_VALUE : rayTraceContext.getFrom().distanceToSqr(fluidResult.getLocation());
            return blockDistance <= fluidDistance ? blockResult : fluidResult;
        }, (rayTraceContext) -> {
            Vec3 Vector3d = rayTraceContext.getFrom().subtract(rayTraceContext.getTo());
            return BlockHitResult.miss(rayTraceContext.getTo(), Direction.getNearest(Vector3d.x, Vector3d.y, Vector3d.z), BlockPos.containing(rayTraceContext.getTo()));
        });
    }

    private static <T> T performRayTrace(ClipContext context, BiFunction<ClipContext, BlockPos, T> hitFunction, Function<ClipContext, T> p_217300_2_) {
        Vec3 startVec = context.getFrom();
        Vec3 endVec = context.getTo();
        if(startVec.equals(endVec)) {
            return p_217300_2_.apply(context);
        }
        else {
            double startX = Mth.lerp(-0.0000001, endVec.x, startVec.x);
            double startY = Mth.lerp(-0.0000001, endVec.y, startVec.y);
            double startZ = Mth.lerp(-0.0000001, endVec.z, startVec.z);
            double endX = Mth.lerp(-0.0000001, startVec.x, endVec.x);
            double endY = Mth.lerp(-0.0000001, startVec.y, endVec.y);
            double endZ = Mth.lerp(-0.0000001, startVec.z, endVec.z);
            int blockX = Mth.floor(endX);
            int blockY = Mth.floor(endY);
            int blockZ = Mth.floor(endZ);
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(blockX, blockY, blockZ);
            T t = hitFunction.apply(context, mutablePos);
            if(t != null) {
                return t;
            }

            double deltaX = startX - endX;
            double deltaY = startY - endY;
            double deltaZ = startZ - endZ;
            int signX = Mth.sign(deltaX);
            int signY = Mth.sign(deltaY);
            int signZ = Mth.sign(deltaZ);
            double d9 = signX == 0 ? Double.MAX_VALUE : (double) signX / deltaX;
            double d10 = signY == 0 ? Double.MAX_VALUE : (double) signY / deltaY;
            double d11 = signZ == 0 ? Double.MAX_VALUE : (double) signZ / deltaZ;
            double d12 = d9 * (signX > 0 ? 1.0D - Mth.frac(endX) : Mth.frac(endX));
            double d13 = d10 * (signY > 0 ? 1.0D - Mth.frac(endY) : Mth.frac(endY));
            double d14 = d11 * (signZ > 0 ? 1.0D - Mth.frac(endZ) : Mth.frac(endZ));

            while(d12 <= 1.0D || d13 <= 1.0D || d14 <= 1.0D) {
                if(d12 < d13) {
                    if(d12 < d14) {
                        blockX += signX;
                        d12 += d9;
                    }
                    else {
                        blockZ += signZ;
                        d14 += d11;
                    }
                }
                else if(d13 < d14) {
                    blockY += signY;
                    d13 += d10;
                }
                else {
                    blockZ += signZ;
                    d14 += d11;
                }

                T t1 = hitFunction.apply(context, mutablePos.set(blockX, blockY, blockZ));
                if(t1 != null) {
                    return t1;
                }
            }

            return p_217300_2_.apply(context);
        }
    }
}
