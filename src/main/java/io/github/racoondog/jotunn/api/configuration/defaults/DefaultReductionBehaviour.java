package io.github.racoondog.jotunn.api.configuration.defaults;

import io.github.racoondog.jotunn.api.configuration.ReductionBehaviour;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public class DefaultReductionBehaviour implements ReductionBehaviour {
    public static final DefaultReductionBehaviour INSTANCE = new DefaultReductionBehaviour();

    protected DefaultReductionBehaviour() {}

    @Override
    public float reduceByDifficulty(float damage, LivingEntity entity, DamageSource damageSource) {
        return switch (entity.getWorld().getDifficulty()) {
            case PEACEFUL -> 0f;
            case EASY -> Math.min(damage / 2f + 1f, damage);
            case NORMAL -> damage;
            case HARD -> damage * 1.5f;
        };
    }
}
