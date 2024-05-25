package io.github.racoondog.jotunn.impl.effects.bruteforce;

import io.github.racoondog.multiversion.MultiVersion;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;

/**
 * Documenting the status effect color changes in 1.19.4
 */
public final class EffectColorRegistry {
    private static final Object2IntMap<RegistryEntry<StatusEffect>> REGISTRY = new Object2IntOpenHashMap<>();
    public static final int V1_19_4 = 762;

    static {
        REGISTRY.put(StatusEffects.SPEED, 0x7CAFC6);
        REGISTRY.put(StatusEffects.SLOWNESS, 0x5A6C81);
        REGISTRY.put(StatusEffects.STRENGTH, 0x932423);
        REGISTRY.put(StatusEffects.JUMP_BOOST, 0x22FF4C);
        REGISTRY.put(StatusEffects.FIRE_RESISTANCE, 0xE49A3A);
        REGISTRY.put(StatusEffects.WATER_BREATHING, 0x2E5299);
        REGISTRY.put(StatusEffects.NIGHT_VISION, 0x1F1FA1);
        REGISTRY.put(StatusEffects.INVISIBILITY, 0x7F8392);
        REGISTRY.put(StatusEffects.POISON, 0x4E9331);
    }

    private EffectColorRegistry() {}

    public static int getColor(RegistryEntry<StatusEffect> effect, boolean shouldUseOldColors) {
        int color = effect.value().getColor();
        return shouldUseOldColors ? REGISTRY.getOrDefault(effect, color) : color;
    }

    public static int getColor(RegistryEntry<StatusEffect> effect) {
        return getColor(effect, MultiVersion.getInstance().isUnder(V1_19_4));
    }
}
