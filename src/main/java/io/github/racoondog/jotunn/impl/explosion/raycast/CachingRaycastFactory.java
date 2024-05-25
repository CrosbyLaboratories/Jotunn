package io.github.racoondog.jotunn.impl.explosion.raycast;

import io.github.racoondog.jotunn.api.explosion.ExposureRaycastContext;
import io.github.racoondog.jotunn.api.explosion.RaycastFactory;
import net.minecraft.block.BlockState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract sealed class CachingRaycastFactory implements RaycastFactory {
    protected final BlockCachingStrategy cachingStrategy;

    public static RaycastFactory get(World world, boolean ignoreTerrain) {
        return ignoreTerrain ? new IgnoreTerrain(world) : new Regular(world);
    }

    protected CachingRaycastFactory(World world) {
        this.cachingStrategy = new BlockCachingStrategy(world);
    }

    private static final class IgnoreTerrain extends CachingRaycastFactory {
        private IgnoreTerrain(World world) {
            super(world);
        }

        @Override
        public BlockHitResult apply(ExposureRaycastContext context, BlockPos blockPos) {
            BlockState blockState = this.cachingStrategy.get(blockPos);
            if (blockState.getBlock().getBlastResistance() < 600) return null;

            return blockState.getCollisionShape(context.world(), blockPos).raycast(context.startPosition(), context.endPosition(), blockPos);
        }
    }

    private static final class Regular extends CachingRaycastFactory {
        private Regular(World world) {
            super(world);
        }

        @Override
        public BlockHitResult apply(ExposureRaycastContext context, BlockPos blockPos) {
            BlockState blockState = this.cachingStrategy.get(blockPos);

            return blockState.getCollisionShape(context.world(), blockPos).raycast(context.startPosition(), context.endPosition(), blockPos);
        }
    }
}
