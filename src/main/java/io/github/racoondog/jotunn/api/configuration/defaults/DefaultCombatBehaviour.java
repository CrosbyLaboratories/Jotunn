package io.github.racoondog.jotunn.api.configuration.defaults;

import io.github.racoondog.jotunn.api.Jotunn;
import io.github.racoondog.jotunn.api.configuration.CombatBehaviour;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

public class DefaultCombatBehaviour implements CombatBehaviour {
    public static final DefaultCombatBehaviour INSTANCE = new DefaultCombatBehaviour();

    protected DefaultCombatBehaviour() {}

    @Override
    public boolean canCriticalHit(PlayerEntity player) {
        return player.fallDistance > 0f
                && !player.isOnGround()
                && !player.isClimbing()
                && !player.isTouchingWater()
                && !Jotunn.effects().hasStatusEffect(player, StatusEffects.BLINDNESS)
                && !player.hasVehicle()
                && !player.isSprinting();
    }
}
