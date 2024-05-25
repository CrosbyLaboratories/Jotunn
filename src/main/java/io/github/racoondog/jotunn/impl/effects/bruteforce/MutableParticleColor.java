package io.github.racoondog.jotunn.impl.effects.bruteforce;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Objects;

public final class MutableParticleColor {
    public float r, g, b;
    public int amplifiers;

    public void add(RegistryEntry<StatusEffect> effect, int amplifier, boolean shouldUseOldColors) {
        int color = EffectColorRegistry.getColor(effect, shouldUseOldColors);
        r += (float)(amplifier * (color >> 16 & 255)) / 255.0F;
        g += (float)(amplifier * (color >> 8 & 255)) / 255.0F;
        b += (float)(amplifier * (color & 255)) / 255.0F;
        amplifiers += amplifier;
    }

    public int pack() {
        float r = this.r / amplifiers * 255f;
        float g = this.g / amplifiers * 255f;
        float b = this.b / amplifiers * 255f;
        return (int) r << 16 | (int) g << 8 | (int) b;
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, g, b, amplifiers);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof MutableParticleColor other) {
            return r == other.r && g == other.g && b == other.b && amplifiers == other.amplifiers;
        }
        return false;
    }
}
