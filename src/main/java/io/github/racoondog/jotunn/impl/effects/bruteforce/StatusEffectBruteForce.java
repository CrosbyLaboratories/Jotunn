package io.github.racoondog.jotunn.impl.effects.bruteforce;

import com.google.common.collect.Sets;
import io.github.racoondog.jotunn.api.configuration.StatusEffectBehaviour;
import io.github.racoondog.jotunn.api.effects.StatusEffectEntry;
import io.github.racoondog.jotunn.impl.effects.StatusEffectsImpl;
import io.github.racoondog.jotunn.impl.mixin.minecraft.EntityEffectParticleEffectAccessor;
import io.github.racoondog.jotunn.impl.mixin.minecraft.LivingEntityAccessor;
import io.github.racoondog.multiversion.MultiVersion;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class StatusEffectBruteForce {
    private static final LongSet NULL_COLORS = new LongOpenHashSet();
    public static final Long2ObjectMap<Map<RegistryEntry<StatusEffect>, StatusEffectInstance>> EFFECT_CACHE = new Long2ObjectOpenHashMap<>();
    private static final int EMPTY_COLOR = 3694022;

    private StatusEffectBruteForce() {}

    public static void bruteForce(StatusEffectBehaviour behaviour, LivingEntity entity, Map<RegistryEntry<StatusEffect>, StatusEffectInstance> effects, List<StatusEffectEntry> possibleEffects) {
        List<ParticleEffect> particles = entity.getDataTracker().get(LivingEntityAccessor.jotunn$getPotionSwirls());
        if (particles.isEmpty() || !(particles.getFirst() instanceof EntityEffectParticleEffect particleEffect)) return; // nothing :pensive:
        int color = ((EntityEffectParticleEffectAccessor) particleEffect).jotunn$getColor();

        if (color == EMPTY_COLOR) return; // todo check whether viaversion remapping keeps empty colors

        MutableParticleColor initialColor = new MutableParticleColor();
        boolean shouldUseNewColors = MultiVersion.getInstance().isAtLeast(EffectColorRegistry.V1_19_4);

        for (StatusEffectInstance appliedEffect : effects.values()) {
            initialColor.add(appliedEffect.getEffectType(), appliedEffect.getAmplifier(), shouldUseNewColors);
        }

        long cacheKey = pack(color, initialColor.pack());

        if (NULL_COLORS.contains(cacheKey)) return;

        // process entries
        Set<ProcessedEffectEntry> processedEntries = new ObjectOpenHashSet<>();
        for (StatusEffectEntry entry : possibleEffects) {
            processedEntries.add(ProcessedEffectEntry.of(entry, shouldUseNewColors));
        }

        @Nullable Map<RegistryEntry<StatusEffect>, StatusEffectInstance> match = EFFECT_CACHE.get(cacheKey);
        if (match == null) {
            @Nullable Set<ProcessedEffectEntry> result = bruteForce(behaviour, initialColor, particleEffect, processedEntries);
            if (result == null) {
                NULL_COLORS.add(cacheKey);
            } else {
                int assumedAmplifier = -1;
                if (effects.isEmpty()) {
                    IntSummaryStatistics summary = result.stream().map(ProcessedEffectEntry::amplifier).distinct().mapToInt(i -> i).summaryStatistics();
                    if (summary.getCount() == 1) {
                        assumedAmplifier = summary.getMax();
                    }
                }

                for (ProcessedEffectEntry entry : result) {
                    effects.put(entry.statusEffect(), StatusEffectsImpl.makeInstance(entry.statusEffect(), assumedAmplifier == -1 ? entry.amplifier() : assumedAmplifier));
                }

                EFFECT_CACHE.put(cacheKey, effects);
            }
        } else {
            effects.putAll(match);
        }
    }

    @Nullable
    private static Set<ProcessedEffectEntry> bruteForce(StatusEffectBehaviour behaviour, MutableParticleColor initialColor, EntityEffectParticleEffect particleEffect, Set<ProcessedEffectEntry> possibleEffects) {
        float targetR = particleEffect.getRed();
        float targetG = particleEffect.getGreen();
        float targetB = particleEffect.getBlue();

        int maxDepth = behaviour.bruteForceMaxDepth();
        for (int depth = 2; depth <= maxDepth; depth++) {
            for (Set<ProcessedEffectEntry> combination : Sets.combinations(possibleEffects, depth)) {
                float r = initialColor.r;
                float g = initialColor.g;
                float b = initialColor.b;
                float a = initialColor.amplifiers;

                for (ProcessedEffectEntry entry : combination) {
                    r += entry.r();
                    g += entry.g();
                    b += entry.b();
                    a += entry.amplifier();
                }

                r /= a;
                g /= a;
                b /= a;

                if (r == targetR && b == targetB && g == targetG) {
                    return combination;
                }
            }
        }

        return null;
    }

    public static long pack(int i1, int i2) {
        return ((long) i1) << 32 | i2;
    }
}
