package io.github.racoondog.jotunn.api.configuration;

import net.minecraft.entity.player.PlayerEntity;

public interface CombatBehaviour {
    /**
     * @param player the {@link PlayerEntity}
     * @return whether the player can critical hit, disregarding current charge.
     */
    boolean canCriticalHit(PlayerEntity player);
}
