package io.github.racoondog.jotunn.api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface Combat {
    /**
     * @param attacker
     * @param target
     * @return the attack damage dealt, assuming maximum charge and a critical hit.
     */
    float maxAttackDamage(LivingEntity attacker, LivingEntity target);

    /**
     * @param attacker
     * @param target
     * @return the attack damage dealt, taking into account current charge and critical hit potential.
     */
    float attackDamage(LivingEntity attacker, LivingEntity target);

    /**
     * @param attacker
     * @param attackTarget
     * @param sweepingTarget
     * @return the sweeping damage dealt
     */
    float sweepingDamage(LivingEntity attacker, LivingEntity attackTarget, LivingEntity sweepingTarget);

    /**
     * @param entity
     * @return whether the player can critical hit, regardless of current charge.
     */
    boolean canCriticalHit(PlayerEntity entity);
}
