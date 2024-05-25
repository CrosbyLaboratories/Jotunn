package io.github.racoondog.jotunn.impl.effects.bruteforce;

import io.github.racoondog.jotunn.api.effects.StatusEffectEntry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

public record ProcessedEffectEntry(RegistryEntry<StatusEffect> statusEffect, int amplifier, float r, float g, float b) {
    public static ProcessedEffectEntry of(StatusEffectEntry effectEntry, boolean shouldUseOldColors) {
        int color = EffectColorRegistry.getColor(effectEntry.statusEffect(), shouldUseOldColors);
        int amplifier = effectEntry.amplifier();
        float r = (float)(amplifier * (color >> 16 & 255)) / 255.0F;
        float g = (float)(amplifier * (color >> 8 & 255)) / 255.0F;
        float b = (float)(amplifier * (color & 255)) / 255.0F;
        return new ProcessedEffectEntry(effectEntry.statusEffect(), amplifier, r, g, b);
    }
}
