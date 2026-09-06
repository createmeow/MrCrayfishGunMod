package com.mrcrayfish.guns.compat;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.mixinterface.clip_overwrite.LevelPoseProviderExtension;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.ClipContext;

import javax.annotation.Nullable;

/**
 * Sable-specific implementation. This class is only loaded when Sable is present,
 * so it is safe to directly reference Sable classes here.
 */
public class SableHelperImpl {

    /**
     * Ray traces blocks including sub-levels. The vanilla raytrace is expected to be
     * done by the caller (SableHelper) before delegating here.
     */
    public static BlockHitResult rayTraceBlocks(Level level, Vec3 start, Vec3 end, ClipContext.Block blockMode, ClipContext.Fluid fluidMode, CollisionContext collisionContext) {
        return rayTraceBlocks(level, start, end, blockMode, fluidMode, collisionContext, state -> false);
    }

    public static BlockHitResult rayTraceBlocks(Level level, Vec3 start, Vec3 end, ClipContext.Block blockMode, ClipContext.Fluid fluidMode, CollisionContext collisionContext, java.util.function.Predicate<BlockState> ignorePredicate) {
        ClipContext context = new ClipContext(start, end, blockMode, fluidMode, collisionContext);
        BlockHitResult result = SableHelper.performRayTrace(level, context, ignorePredicate);

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

            if(Sable.HELPER.getContaining(level, plotStart) != subLevel) {
                continue;
            }

            ClipContext subContext = new ClipContext(plotStart, plotEnd, blockMode, fluidMode, collisionContext);
            BlockHitResult subResult = SableHelper.performRayTrace(subLevel.getLevel(), subContext, ignorePredicate);
            double distance = subResult.getLocation().distanceTo(plotStart);

            if(subResult.getType() != HitResult.Type.MISS && (distance < minDistance || minResult.getType() == HitResult.Type.MISS)) {
                minResult = subResult;
                minDistance = distance;
            }
        }

        return minResult;
    }

    public static boolean isInPlotGrid(Level level, BlockPos pos) {
        return Sable.HELPER.isInPlotGrid(level, pos.getX(), pos.getZ());
    }

    @Nullable
    private static SubLevel getSubLevel(Level level, BlockPos plotPos) {
        SubLevel subLevel = Sable.HELPER.getContaining(level, plotPos);
        return subLevel != null && !subLevel.isRemoved() ? subLevel : null;
    }

    @Nullable
    public static BlockState getSubLevelState(Level level, BlockPos plotPos) {
        SubLevel subLevel = getSubLevel(level, plotPos);
        return subLevel != null ? subLevel.getLevel().getBlockState(plotPos) : null;
    }

    @Nullable
    public static Level getSubLevelLevel(Level level, BlockPos plotPos) {
        SubLevel subLevel = getSubLevel(level, plotPos);
        return subLevel != null ? subLevel.getLevel() : null;
    }

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
}
