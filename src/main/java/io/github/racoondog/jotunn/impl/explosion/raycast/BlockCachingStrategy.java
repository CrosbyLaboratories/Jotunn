package io.github.racoondog.jotunn.impl.explosion.raycast;

import io.github.racoondog.jotunn.impl.Utils;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

import static io.github.racoondog.jotunn.impl.Utils.*;

/**
 * Not thread-safe and stateful, do not save this in a variable and use it across several ticks.
 * Implements {@link Long2ObjectFunction} to remove {@code INVOKEDYNAMIC} instruction in {@link #get(BlockPos)}.
 *
 * @author Crosby
 */
public final class BlockCachingStrategy implements Long2ObjectFunction<BlockState> {
    private final Long2ObjectMap<BlockState> blockStateCache = new Long2ObjectOpenHashMap<>(Hash.DEFAULT_INITIAL_SIZE, Hash.FAST_LOAD_FACTOR);
    private final World world;
    private @Nullable WorldChunk prevChunk = null;
    private int prevChunkX = Integer.MIN_VALUE, prevChunkZ = Integer.MIN_VALUE;

    public BlockCachingStrategy(World world) {
        this.world = world;
    }

    public BlockState get(BlockPos pos) {
        long key = pos.asLong();
        return blockStateCache.computeIfAbsent(key, this);
    }

    @Override
    public BlockState get(long key) {
        int x = (int) ((key >> BIT_SHIFT_X) & BITS_X);
        int z = (int) ((key >> BIT_SHIFT_Z) & BITS_Z);

        int chunkX = ChunkSectionPos.getSectionCoord(x);
        int chunkZ = ChunkSectionPos.getSectionCoord(z);

        WorldChunk chunk;
        if (chunkX == prevChunkX && chunkZ == prevChunkZ) {
            chunk = prevChunk;
            if (chunk == null) return VOID_AIR;
        } else {
            chunk = prevChunk = world.getChunk(chunkX, chunkZ);
            prevChunkX = chunkX;
            prevChunkZ = chunkZ;
        }

        int y = (int) (key & BITS_Y);

        return Utils.getBlockState(chunk, x, y, z);
    }
}
