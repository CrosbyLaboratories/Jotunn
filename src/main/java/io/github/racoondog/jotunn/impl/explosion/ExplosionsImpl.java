package io.github.racoondog.jotunn.impl.explosion;

import io.github.racoondog.jotunn.api.Explosions;
import io.github.racoondog.jotunn.api.Jotunn;
import io.github.racoondog.jotunn.api.explosion.ExposureRaycastContext;
import io.github.racoondog.jotunn.api.explosion.RaycastFactory;
import io.github.racoondog.jotunn.impl.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

public final class ExplosionsImpl implements Explosions {
    public static final ExplosionsImpl INSTANCE = new ExplosionsImpl();

    private ExplosionsImpl() {}

    @Override
    public float explosionDamage(LivingEntity target, Explosion explosion, RaycastFactory raycastFactory) {
        return explosionDamage(target, target.getPos(), target.getBoundingBox(), explosion, raycastFactory);
    }

    @Override
    public float explosionDamage(LivingEntity target, Vec3d explosionPos, float power, RaycastFactory raycastFactory) {
        return explosionDamage(target, target.getPos(), target.getBoundingBox(), explosionPos, power, raycastFactory);
    }

    @Override
    public float explosionDamage(LivingEntity target, Vec3d targetPos, Box targetBox, Explosion explosion, RaycastFactory raycastFactory) {
        return explosionDamage(target, targetPos, targetBox, explosion.getPosition(), explosion.getPower(), raycastFactory);
    }

    /**
     * @see net.minecraft.world.explosion.ExplosionBehavior#calculateDamage(Explosion, Entity)
     */
    @Override
    public float explosionDamage(LivingEntity target, Vec3d targetPos, Box targetBox, Vec3d explosionPos, float power, RaycastFactory raycastFactory) {
        double modDistance = Utils.distance(targetPos.x, targetPos.y, targetPos.z, explosionPos.x, explosionPos.y, explosionPos.z);
        if (modDistance > power) return 0f;

        double exposure = getExposure(target.getWorld(), explosionPos, targetBox, raycastFactory);
        double impact = (1 - modDistance / power) * exposure;
        float damage = (float) ((impact * impact + impact) / 2d * 7d * power + 1d);

        return Jotunn.reductions().reduce(damage, target, target.getWorld().getDamageSources().explosion(null));
    }

    /**
     * Rewrite of Minecraft's {@link Explosion#getExposure(Vec3d, Entity)} with better performance and with a
     * configurable {@link RaycastFactory}.
     * It is possible to improve the performance of this method slightly in the event that only
     * {@link net.minecraft.entity.player.PlayerEntity} are to be supported by assuming that {@code xOffset == zOffset}
     * and {@code xStep == zStep}, because {@code xDiff == zDiff}. However, this is not done here as that would break
     * calculations with other entities of uneven bounding boxes.
     *
     * @author Crosby
     * @since 0.1.0
     * @see Explosion#getExposure(Vec3d, Entity)
     */
    @Override
    public float getExposure(World world, Vec3d source, Box box, RaycastFactory raycastFactory) {
        double xDiff = box.maxX - box.minX;
        double yDiff = box.maxY - box.minY;
        double zDiff = box.maxZ - box.minZ;

        double xStep = 1 / (xDiff * 2 + 1);
        double yStep = 1 / (yDiff * 2 + 1);
        double zStep = 1 / (zDiff * 2 + 1);

        if (xStep > 0 && yStep > 0 && zStep > 0) {
            int misses = 0;
            int hits = 0;

            double xOffset = (1 - Math.floor(1 / xStep) * xStep) * 0.5;
            double zOffset = (1 - Math.floor(1 / zStep) * zStep) * 0.5;

            xStep = xStep * xDiff;
            yStep = yStep * yDiff;
            zStep = zStep * zDiff;

            double startX = box.minX + xOffset;
            double startY = box.minY;
            double startZ = box.minZ + zOffset;
            double endX = box.maxX + xOffset;
            double endY = box.maxY;
            double endZ = box.maxZ + zOffset;

            for (double x = startX; x <= endX; x += xStep) {
                for (double y = startY; y <= endY; y += yStep) {
                    for (double z = startZ; z <= endZ; z += zStep) {
                        Vec3d position = new Vec3d(x, y, z);

                        if (raycast(new ExposureRaycastContext(world, position, source), raycastFactory) == null) misses++;

                        hits++;
                    }
                }
            }

            return (float) misses / hits;
        }

        return 0f;
    }

    @Nullable
    private static BlockHitResult raycast(ExposureRaycastContext context, RaycastFactory factory) {
        return BlockView.raycast(context.startPosition(), context.endPosition(), context, factory, ctx -> null);
    }
}
