package io.github.racoondog.jotunn.api.configuration;

import io.github.racoondog.jotunn.api.configuration.defaults.DefaultReductionBehaviour;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public interface ReductionBehaviour {
    /**
     * Override this to modify the difficulty provided by the server. For example, you can modify the behaviour to
     * assume that end crystal explosions will still deal damage even in peaceful.
     *
     * @param damage the incoming damage
     * @param entity the {@link LivingEntity} receiving the damage
     * @param damageSource the source of the damage
     * @return the damage after reduction by difficulty
     * @since 0.1.0
     * @see DefaultReductionBehaviour#reduceByDifficulty(float, LivingEntity, DamageSource)
     */
    float reduceByDifficulty(float damage, LivingEntity entity, DamageSource damageSource);
}
