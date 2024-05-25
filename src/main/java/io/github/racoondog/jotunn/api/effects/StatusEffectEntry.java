package io.github.racoondog.jotunn.api.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

/**
 * A status effect & amplifier pair that can be encountered in-game.
 *
 * @param statusEffect the status effect.
 * @param amplifier one-indexed (not the same as the /effect command, which is zero indexed) amplifier.
 * @since 0.1.0
 */
public record StatusEffectEntry(RegistryEntry<StatusEffect> statusEffect, int amplifier) { }
