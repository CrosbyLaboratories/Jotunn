package io.github.racoondog.jotunn.api.explosion;

import io.github.racoondog.jotunn.impl.explosion.raycast.CachingRaycastFactory;
import io.github.racoondog.jotunn.impl.explosion.raycast.SimpleRaycastFactory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.BiFunction;

/**
 * The static methods in this class provide reference implementations of {@link RaycastFactory} that are provided with
 * the library. You should implement your own subclass if you want custom behaviour.
 *
 * @since 0.1.0
 */
@FunctionalInterface
public interface RaycastFactory extends BiFunction<ExposureRaycastContext, BlockPos, BlockHitResult> {
    /**
     * @param ignoreTerrain whether to replace breakable blocks with air.
     * @return a stateless {@link RaycastFactory}.
     * @since 0.1.0
     */
    static RaycastFactory of(boolean ignoreTerrain) {
        return SimpleRaycastFactory.get(ignoreTerrain);
    }

    /**
     * The {@link RaycastFactory} returned by this method is not thread-safe and stateful. You should not use it across
     * several threads, and you should not save it in a variable to use on a separate tick.
     *
     * @param ignoreTerrain whether to replace breakable blocks with air.
     * @return a caching {@link RaycastFactory}.
     * @since 0.1.0
     */
    static RaycastFactory ofCaching(World world, boolean ignoreTerrain) {
        return CachingRaycastFactory.get(world, ignoreTerrain);
    }
}
