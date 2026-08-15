package com.mrcrayfish.guns.compat;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.mixinterface.clip_overwrite.LevelPoseProviderExtension;
import dev.ryanhcode.sable.sublevel.SubLevel;
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
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.fml.ModList;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Integration with the Sable (sub-levels) library mod. Allows gun projectiles to collide
 * with blocks living inside sub-levels instead of passing straight through their plots.
 * <p>
 * The raycast approach mirrors Sable's own {@code BlockGetter.clip} overwrite and the
 * Create compatibility helper: raycast the main level first, then raycast each intersecting
 * sub-level in its plot space and keep the closest hit.
 *
 * @author MrCrayfish (compat layer)
 */
public class SableHelper {

    public static boolean isSableLoaded() {
        return ModList.get() != null && ModList.get().isLoaded("sable");
    }

    /**
     * Ray traces blocks in the given level, including blocks stored inside any intersecting
     * sub-levels. If Sable is not loaded, this behaves exactly like the original custom
     * implementation found in {@code ProjectileEntity}.
     *
     * @param level            the level to ray trace
     * @param start            the start position of the ray
     * @param end              the end position of the ray
     * @param blockMode        the block collision mode for the ray trace
     * @param fluidMode        the fluid collision mode for the ray trace
     * @param collisionContext the collision context used for shape queries
     * @param ignorePredicate  block states to ignore while ray tracing
     * @return the closest block hit, either in the main level or in a sub-level
     */
    public static BlockHitResult rayTraceBlocks(Level level, Vec3 start, Vec3 end, ClipContext.Block blockMode, ClipContext.Fluid fluidMode, CollisionContext collisionContext, Predicate<BlockState> ignorePredicate) {
        ClipContext context = new ClipContext(start, end, blockMode, fluidMode, collisionContext);
        BlockHitResult result = performRayTrace(level, context, ignorePredicate);
        if(!isSableLoaded()) {
            return result;
        }

        double minDistance = result.getLocation().distanceTo(start);
        BlockHitResult minResult = result;

        Iterable<SubLevel> subLevels = Sable.HELPER.getAllIntersecting(level, new BoundingBox3d(start, end));
        for(SubLevel subLevel : subLevels) {
            if(subLevel.isRemoved()) {
                continue;
            }

            Pose3dc pose = subLevel.logicalPose();
            if(level instanceof LevelPoseProviderExtension extension) {
                pose = extension.sable$getPose(subLevel);
            }

            Vec3 plotStart = pose.transformPositionInverse(start);
            Vec3 plotEnd = pose.transformPositionInverse(end);

            /* The ray is only traced inside a sub-level if it starts within its plot */
            if(Sable.HELPER.getContaining(level, plotStart) != subLevel) {
                continue;
            }

            ClipContext subContext = new ClipContext(plotStart, plotEnd, blockMode, fluidMode, collisionContext);
            BlockHitResult subResult = performRayTrace(subLevel.getLevel(), subContext, ignorePredicate);
            double distance = subResult.getLocation().distanceTo(plotStart);

            if(subResult.getType() != HitResult.Type.MISS && (distance < minDistance || minResult.getType() == HitResult.Type.MISS)) {
                minResult = subResult;
                minDistance = distance;
            }
        }

        return minResult;
    }

    /**
     * Checks if the given position (in plot space) is inside the plot grid of any sub-level.
     */
    public static boolean isInPlotGrid(Level level, BlockPos pos) {
        return isSableLoaded() && Sable.HELPER.isInPlotGrid(level, pos);
    }

    /**
     * Gets the sub-level that contains the given plot-space position, or null if none does.
     *
     * @return the containing sub-level, or null if the position is not inside a loaded sub-level plot
     */
    @Nullable
    public static SubLevel getSubLevel(Level level, BlockPos plotPos) {
        if(!isSableLoaded()) {
            return null;
        }
        SubLevel subLevel = Sable.HELPER.getContaining(level, plotPos);
        return subLevel != null && !subLevel.isRemoved() ? subLevel : null;
    }

    /**
     * Gets the level of the sub-level that contains the given plot-space position, or null if none does.
     *
     * @return the sub-level's own level, or null if the position is not inside a loaded sub-level plot
     */
    @Nullable
    public static Level getSubLevelLevel(Level level, BlockPos plotPos) {
        SubLevel subLevel = getSubLevel(level, plotPos);
        return subLevel != null ? subLevel.getLevel() : null;
    }

    /**
     * Gets the actual block state stored at the given plot-space position, if it belongs to a sub-level.
     *
     * @return the sub-level block state, or null if the position is not inside a sub-level plot
     */
    @Nullable
    public static BlockState getSubLevelState(Level level, BlockPos plotPos) {
        SubLevel subLevel = getSubLevel(level, plotPos);
        return subLevel != null ? subLevel.getLevel().getBlockState(plotPos) : null;
    }

    /**
     * Projects a plot-space position out of its sub-level into global space. If the position is
     * not inside a sub-level, the position is returned unchanged.
     */
    public static Vec3 projectOutOfSubLevel(Level level, Vec3 plotPos) {
        SubLevel subLevel = Sable.HELPER.getContaining(level, plotPos);
        if(subLevel == null || subLevel.isRemoved()) {
            return plotPos;
        }

        Pose3dc pose = subLevel.logicalPose();
        if(level instanceof LevelPoseProviderExtension extension) {
            pose = extension.sable$getPose(subLevel);
        }
        return pose.transformPosition(plotPos);
    }

    /**
     * A custom implementation of ray tracing that allows you to pass a predicate to ignore certain
     * blocks when checking for collisions. Ported from {@code ProjectileEntity}.
     */
    private static BlockHitResult performRayTrace(Level world, ClipContext context, Predicate<BlockState> ignorePredicate) {
        return performRayTrace(context, (rayTraceContext, blockPos) -> {
            BlockState blockState = world.getBlockState(blockPos);
            if(ignorePredicate.test(blockState)) return null;
            FluidState fluidState = world.getFluidState(blockPos);
            Vec3 startVec = rayTraceContext.getFrom();
            Vec3 endVec = rayTraceContext.getTo();
            VoxelShape blockShape = rayTraceContext.getBlockShape(blockState, world, blockPos);
            BlockHitResult blockResult = world.clipWithInteractionOverride(startVec, endVec, blockPos, blockShape, blockState);
            VoxelShape fluidShape = rayTraceContext.getFluidShape(fluidState, world, blockPos);
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
