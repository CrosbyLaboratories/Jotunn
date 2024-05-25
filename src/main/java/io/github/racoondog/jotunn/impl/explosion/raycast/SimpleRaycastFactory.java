package io.github.racoondog.jotunn.impl.explosion.raycast;

import io.github.racoondog.jotunn.api.explosion.ExposureRaycastContext;
import io.github.racoondog.jotunn.api.explosion.RaycastFactory;
import io.github.racoondog.jotunn.impl.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.WorldChunk;

public abstract sealed class SimpleRaycastFactory implements RaycastFactory {
    public static RaycastFactory get(boolean ignoreTerrain) {
        return ignoreTerrain ? IgnoreTerrain.INSTANCE : Regular.INSTANCE;
    }

    protected SimpleRaycastFactory() {}

    private static final class IgnoreTerrain extends SimpleRaycastFactory {
        public static final IgnoreTerrain INSTANCE = new IgnoreTerrain();

        private IgnoreTerrain() {}

        @SuppressWarnings("resource")
        @Override
        public BlockHitResult apply(ExposureRaycastContext context, BlockPos pos) {
            WorldChunk worldChunk = context.world().getChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
            BlockState blockState = Utils.getBlockState(worldChunk, pos.getX(), pos.getY(), pos.getZ());
            if (blockState.getBlock().getBlastResistance() < 600) return null;

            return blockState.getCollisionShape(context.world(), pos).raycast(context.startPosition(), context.endPosition(), pos);
        }
    }

    private static final class Regular extends SimpleRaycastFactory {
        public static final Regular INSTANCE = new Regular();

        private Regular() {}

        @SuppressWarnings("resource")
        @Override
        public BlockHitResult apply(ExposureRaycastContext context, BlockPos pos) {
            WorldChunk worldChunk = context.world().getChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
            BlockState blockState = Utils.getBlockState(worldChunk, pos.getX(), pos.getY(), pos.getZ());

            return blockState.getCollisionShape(context.world(), pos).raycast(context.startPosition(), context.endPosition(), pos);
        }
    }
}
