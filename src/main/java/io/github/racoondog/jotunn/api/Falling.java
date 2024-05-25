package io.github.racoondog.jotunn.api;

import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface Falling {
    double fallHeight(LivingEntity entity);

    float fallDamage(LivingEntity entity);

    float reduceFallDamage(LivingEntity entity, double fallHeight);
}
