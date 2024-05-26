package io.github.racoondog.jotunn.api.configuration.defaults;

import io.github.racoondog.jotunn.api.configuration.StatusEffectBehaviour;
import io.github.racoondog.jotunn.api.effects.StatusEffectEntry;
import io.github.racoondog.jotunn.impl.effects.bruteforce.DefaultStatusEffectEntries;
import io.github.racoondog.multiversion.MultiVersion;
import net.minecraft.entity.effect.StatusEffects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultStatusEffectBehaviour implements StatusEffectBehaviour {
    public static final DefaultStatusEffectBehaviour INSTANCE = new DefaultStatusEffectBehaviour();
    protected static final StatusEffectEntry ABSORPTION_WEAK = new StatusEffectEntry(StatusEffects.ABSORPTION, 1);
    protected static final StatusEffectEntry ABSORPTION_MEDIUM = new StatusEffectEntry(StatusEffects.ABSORPTION, 2);
    protected static final StatusEffectEntry ABSORPTION_STRONG = new StatusEffectEntry(StatusEffects.ABSORPTION, 4);

    protected DefaultStatusEffectBehaviour() {}

    @Override
    public Collection<StatusEffectEntry> getEntries() {
        return DefaultStatusEffectEntries.getEntries();
    }

    @Override
    public Collection<StatusEffectEntry> getBeaconEntries() {
        return DefaultStatusEffectEntries.getBeaconEntries();
    }

    @Override
    public List<StatusEffectEntry> handleAbsorption(float absorptionHealth) {
        if (absorptionHealth == 0f) return List.of();

        List<StatusEffectEntry> entries = new ArrayList<>(3);
        if (absorptionHealth <= 16f) entries.add(ABSORPTION_STRONG); // enchanted golden apple
        if (absorptionHealth <= 8f && MultiVersion.getInstance().isAtLeast(MultiVersion.V1_11)) entries.add(ABSORPTION_MEDIUM); // totem of undying
        if (absorptionHealth <= 4f) entries.add(ABSORPTION_WEAK); // golden apple

        return entries;
    }
}
