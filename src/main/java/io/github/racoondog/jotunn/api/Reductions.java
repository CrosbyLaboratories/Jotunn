package io.github.racoondog.jotunn.api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface Reductions {
    float reduce(float damage, LivingEntity entity, DamageSource damageSource);

    float reduceByArmor(float damage, LivingEntity entity, DamageSource damageSource);

    float reduceByEffects(float damage, LivingEntity entity, DamageSource damageSource);

    float reduceByEnchantments(float damage, LivingEntity entity, DamageSource damageSource);
}
