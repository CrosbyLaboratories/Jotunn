package io.github.racoondog.jotunn.impl;

import io.github.racoondog.jotunn.api.Falling;
import io.github.racoondog.jotunn.api.Jotunn;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;

public final class FallingImpl implements Falling {
    public static final FallingImpl INSTANCE = new FallingImpl();

    private FallingImpl() {}

    @Override
    public double fallHeight(LivingEntity entity) { // todo make more accurate
        // Fast path - Above the surface
        int surface = entity.getWorld().getWorldChunk(entity.getBlockPos()).getHeightmap(Heightmap.Type.MOTION_BLOCKING).get(entity.getBlockX() & 15, entity.getBlockZ() & 15);
        if (entity.getBlockY() >= surface) return getFallHeight(entity, surface);

        // Under the surface
        BlockHitResult raycastResult = entity.getWorld().raycast(new RaycastContext(entity.getPos(), new Vec3d(entity.getX(), entity.getWorld().getBottomY(), entity.getZ()), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.WATER, entity));
        if (raycastResult.getType() == HitResult.Type.MISS) return 0;

        return getFallHeight(entity, raycastResult.getBlockPos().getY());
    }

    private static double getFallHeight(LivingEntity entity, int surfacePos) {
        return entity.getY() - surfacePos + entity.fallDistance - 3d;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public float fallDamage(LivingEntity entity) {
        if (entity instanceof PlayerEntity playerEntity && playerEntity.getAbilities().flying) return 0f;
        if (Jotunn.effects().hasStatusEffect(entity, StatusEffects.SLOW_FALLING) || Jotunn.effects().hasStatusEffect(entity, StatusEffects.LEVITATION)) return 0f;

        double fallHeight = fallHeight(entity);

        return fallHeight <= 0d ? 0f : reduceFallDamage(entity, fallHeight);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public float reduceFallDamage(LivingEntity entity, double fallHeight) {
        @Nullable StatusEffectInstance jumpBoostInstance = Jotunn.effects().getStatusEffect(entity, StatusEffects.JUMP_BOOST);
        if (jumpBoostInstance != null) fallHeight -= jumpBoostInstance.getAmplifier() + 1;

        return Jotunn.reductions().reduce((int) fallHeight, entity, entity.getWorld().getDamageSources().fall());
    }
}
