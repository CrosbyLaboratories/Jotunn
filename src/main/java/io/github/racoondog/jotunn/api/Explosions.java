package io.github.racoondog.jotunn.api;

import io.github.racoondog.jotunn.api.explosion.RaycastFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface Explosions {
    float CRYSTAL_POWER = 12f;
    float BED_POWER = 10f;
    float ANCHOR_POWER = 10f;

    float explosionDamage(LivingEntity target, Explosion explosion, RaycastFactory raycastFactory);

    float explosionDamage(LivingEntity target, Vec3d explosionPos, float power, RaycastFactory raycastFactory);

    float explosionDamage(LivingEntity target, Vec3d targetPos, Box targetBox, Explosion explosion, RaycastFactory raycastFactory);

    float explosionDamage(LivingEntity target, Vec3d targetPos, Box targetBox, Vec3d explosionPos, float power, RaycastFactory raycastFactory);

    float getExposure(World world, Vec3d source, Box box, RaycastFactory raycastFactory);
}
