package io.github.racoondog.jotunn.impl.effects.bruteforce;

import io.github.racoondog.jotunn.api.effects.StatusEffectEntry;
import io.github.racoondog.multiversion.MultiVersion;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public final class DefaultStatusEffectEntries {
    private static final List<StatusEffectEntry> BEACON_ENTRIES = new CopyOnWriteArrayList<>(List.of(
            new StatusEffectEntry(StatusEffects.STRENGTH, 1),
            new StatusEffectEntry(StatusEffects.STRENGTH, 2),
            new StatusEffectEntry(StatusEffects.JUMP_BOOST, 1),
            new StatusEffectEntry(StatusEffects.JUMP_BOOST, 2),
            new StatusEffectEntry(StatusEffects.REGENERATION, 1),
            new StatusEffectEntry(StatusEffects.REGENERATION, 2), // even though you cant choose this in vanilla, there's nothing stopping you from packet manipulation
            new StatusEffectEntry(StatusEffects.RESISTANCE, 1),
            new StatusEffectEntry(StatusEffects.RESISTANCE, 2)
    ));

    private static final Int2ObjectMap<Set<StatusEffectEntry>> ENTRY_CACHE = new Int2ObjectArrayMap<>();

    private static final int V1_16_2 = 751;

    private DefaultStatusEffectEntries() {}

    public static Collection<StatusEffectEntry> getBeaconEntries() {
        return BEACON_ENTRIES;
    }

    public static Collection<StatusEffectEntry> getEntries() {
        return ENTRY_CACHE.computeIfAbsent(MultiVersion.getInstance().getProtocolVersion(), DefaultStatusEffectEntries::initializeEntries);
    }

    private static Set<StatusEffectEntry> initializeEntries(int protocolVersion) { // todo support bedrock effects for viabedrock compat
        Set<StatusEffectEntry> entries = new ObjectOpenHashSet<>();

        // todo can i even remove these
        //entries.add(new StatusEffectEntry(StatusEffects.SPEED, 1)); tracked attribute
        //entries.add(new StatusEffectEntry(StatusEffects.SPEED, 2)); tracked attribute
        //entries.add(new StatusEffectEntry(StatusEffects.SLOWNESS, 1)); tracked attribute
        //entries.add(new StatusEffectEntry(StatusEffects.SLOWNESS, 4)); tracked attribute
        //entries.add(new StatusEffectEntry(StatusEffects.SLOWNESS, 6)); tracked attribute
        //entries.add(new StatusEffectEntry(StatusEffects.HASTE, 1)); tracked attribute
        //entries.add(new StatusEffectEntry(StatusEffects.HASTE, 2)); tracked attribute
        //entries.add(new StatusEffectEntry(StatusEffects.MINING_FATIGUE, 1)); tracked attribute
        //entries.add(new StatusEffectEntry(StatusEffects.HEALTH_BOOST, 1)); tracked attribute
        entries.add(new StatusEffectEntry(StatusEffects.NAUSEA, 1));
        entries.add(new StatusEffectEntry(StatusEffects.FIRE_RESISTANCE, 1));
        entries.add(new StatusEffectEntry(StatusEffects.WATER_BREATHING, 1));
        entries.add(new StatusEffectEntry(StatusEffects.BLINDNESS, 1));
        entries.add(new StatusEffectEntry(StatusEffects.NIGHT_VISION, 1));
        entries.add(new StatusEffectEntry(StatusEffects.HUNGER, 1));
        entries.add(new StatusEffectEntry(StatusEffects.WEAKNESS, 1));
        entries.add(new StatusEffectEntry(StatusEffects.POISON, 1));
        entries.add(new StatusEffectEntry(StatusEffects.POISON, 2));
        entries.add(new StatusEffectEntry(StatusEffects.WITHER, 1));
        entries.add(new StatusEffectEntry(StatusEffects.WITHER, 2)); // todo not on easy difficulty

        if (MultiVersion.getInstance().isUnder(MultiVersion.V1_9)) {
            entries.add(new StatusEffectEntry(StatusEffects.REGENERATION, 5)); // enchanted golden apples
        }

        if (MultiVersion.getInstance().isAtLeast(MultiVersion.V1_9)) {
            entries.add(new StatusEffectEntry(StatusEffects.LEVITATION, 1));
        }

        if (MultiVersion.getInstance().isAtLeast(MultiVersion.V1_13)) {
            entries.add(new StatusEffectEntry(StatusEffects.SLOW_FALLING, 1));
            entries.add(new StatusEffectEntry(StatusEffects.CONDUIT_POWER, 1));
            entries.add(new StatusEffectEntry(StatusEffects.DOLPHINS_GRACE, 1));
            entries.add(new StatusEffectEntry(StatusEffects.RESISTANCE, 3)); // turtle master potion
            entries.add(new StatusEffectEntry(StatusEffects.RESISTANCE, 4)); // turtle master potion
        }

        if (MultiVersion.getInstance().isAtLeast(MultiVersion.V1_14)) {
            entries.add(new StatusEffectEntry(StatusEffects.BAD_OMEN, 1));
            entries.add(new StatusEffectEntry(StatusEffects.HERO_OF_THE_VILLAGE, 1));
            entries.add(new StatusEffectEntry(StatusEffects.SATURATION, 1)); // suspicious stew
        }

        if (MultiVersion.getInstance().isUnder(V1_16_2)) {
            entries.add(new StatusEffectEntry(StatusEffects.NAUSEA, 2)); // pufferfish
        }

        if (MultiVersion.getInstance().getProtocolVersion() == V1_16_2) {
            entries.add(new StatusEffectEntry(StatusEffects.FIRE_RESISTANCE, 2)); // totem (20w28a)
        }

        if (MultiVersion.getInstance().isUnder(MultiVersion.V1_17)) {
            entries.add(new StatusEffectEntry(StatusEffects.POISON, 4)); // pufferfish
        }

        if (MultiVersion.getInstance().isAtLeast(MultiVersion.V1_19)) {
            entries.add(new StatusEffectEntry(StatusEffects.DARKNESS, 1));
        }

        // primitive modded compat
        Registries.STATUS_EFFECT.streamEntries()
                .filter(entry -> !entry.value().isInstant())
                .filter(entry -> !entry.registryKey().getValue().getNamespace().equals("minecraft"))
                .map(entry -> new StatusEffectEntry(entry, 1))
                .forEach(entries::add);

        Registries.POTION.streamEntries()
                .filter(entry -> !entry.registryKey().getValue().getNamespace().equals("minecraft")) // handled manually above
                .flatMap(entry -> entry.value().getEffects().stream())
                .filter(StatusEffectInstance::shouldShowParticles) // brute force requires particles
                .filter(instance -> !instance.getEffectType().value().isInstant())
                .map(instance -> new StatusEffectEntry(instance.getEffectType(), instance.getAmplifier() + 1))
                .forEach(entries::add);

        Registries.ITEM.streamEntries()
                .filter(entry -> !entry.registryKey().getValue().getNamespace().equals("minecraft")) // handled manually above
                .map(RegistryEntry.Reference::value)
                .forEach(item -> computeItem(entries, item));

        return entries;
    }

    private static void computeItem(Set<StatusEffectEntry> entries, Item item) {
        SuspiciousStewEffectsComponent suspiciousStewEffectsComponent = item.getComponents().get(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS);
        if (suspiciousStewEffectsComponent != null) {
            for (SuspiciousStewEffectsComponent.StewEffect effect : suspiciousStewEffectsComponent.effects()) {
                if (!effect.effect().value().isInstant()) {
                    entries.add(new StatusEffectEntry(effect.effect(), 1));
                }
            }
        }

        PotionContentsComponent potionContentsComponent = item.getComponents().get(DataComponentTypes.POTION_CONTENTS);
        if (potionContentsComponent != null) {
            for (StatusEffectInstance instance : potionContentsComponent.getEffects()) {
                if (instance.shouldShowParticles() && !instance.getEffectType().value().isInstant()) {
                    entries.add(new StatusEffectEntry(instance.getEffectType(), instance.getAmplifier() + 1));
                }
            }
        }

        FoodComponent foodComponent = item.getComponents().get(DataComponentTypes.FOOD);
        if (foodComponent != null) {
            for (FoodComponent.StatusEffectEntry entry : foodComponent.effects()) {
                StatusEffectInstance instance = entry.effect();
                if (instance.shouldShowParticles() && !instance.getEffectType().value().isInstant()) {
                    entries.add(new StatusEffectEntry(instance.getEffectType(), instance.getAmplifier() + 1));
                }
            }
        }
    }
}
