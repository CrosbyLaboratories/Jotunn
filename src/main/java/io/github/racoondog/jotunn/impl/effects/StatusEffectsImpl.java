package io.github.racoondog.jotunn.impl.effects;

import io.github.racoondog.jotunn.api.StatusEffects;
import io.github.racoondog.jotunn.api.configuration.StatusEffectBehaviour;
import io.github.racoondog.jotunn.api.effects.StatusEffectEntry;
import io.github.racoondog.jotunn.impl.effects.bruteforce.StatusEffectBruteForce;
import io.github.racoondog.jotunn.impl.mixin.minecraft.LivingEntityAccessor;
import io.github.racoondog.multiversion.MultiVersion;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class StatusEffectsImpl implements StatusEffects {
    private static final StatusEffectEntry INVISIBILITY = new StatusEffectEntry(net.minecraft.entity.effect.StatusEffects.INVISIBILITY, 1);
    private final StatusEffectBehaviour behaviour;

    public StatusEffectsImpl(StatusEffectBehaviour behaviour) {
        this.behaviour = behaviour;
    }

    @Override
    public Collection<StatusEffectInstance> getStatusEffects(LivingEntity entity) {
        return getActiveStatusEffects(entity).values();
    }

    @Override
    public boolean hasStatusEffect(LivingEntity entity, RegistryEntry<StatusEffect> effect) {
        return getActiveStatusEffects(entity).containsKey(effect);
    }

    @Override
    public @Nullable StatusEffectInstance getStatusEffect(LivingEntity entity, RegistryEntry<StatusEffect> effect) {
        return getActiveStatusEffects(entity).get(effect);
    }

    @Override
    public Map<RegistryEntry<StatusEffect>, StatusEffectInstance> getActiveStatusEffects(LivingEntity entity) {
        if (entity == MinecraftClient.getInstance().player) return entity.getActiveStatusEffects();

        if (MultiVersion.getInstance().isAtLeast(MultiVersion.V1_20_5)) {
            return entity.getActiveStatusEffects();
        } else {
            Map<RegistryEntry<StatusEffect>, StatusEffectInstance> effects = new Object2ObjectOpenHashMap<>(); // todo figure out if reference keys are possible
            boolean beacon = entity.getDataTracker().get(LivingEntityAccessor.jotunn$getPotionSwirlsAmbient());

            EffectAttributeModifiers.getFromAttributes(effects, entity, beacon);

            List<StatusEffectEntry> possibleStatusEffects = new ArrayList<>(behaviour.getBeaconEntries());
            if (!beacon) possibleStatusEffects.addAll(behaviour.getEntries());

            if (entity.isGlowing()) {
                StatusEffectInstance effectInstance = makeInstance(net.minecraft.entity.effect.StatusEffects.GLOWING, 1);
                effects.put(net.minecraft.entity.effect.StatusEffects.GLOWING, effectInstance);
            }
            if (entity.isInvisible()) {
                if (entity instanceof ArmorStandEntity) {
                    possibleStatusEffects.add(INVISIBILITY);
                } else {
                    StatusEffectInstance effectInstance = makeInstance(net.minecraft.entity.effect.StatusEffects.INVISIBILITY, 1);
                    effects.put(net.minecraft.entity.effect.StatusEffects.INVISIBILITY, effectInstance);
                }
            }

            if (behaviour.bruteForceEnabled()) {
                possibleStatusEffects.addAll(behaviour.handleAbsorption(entity.getAbsorptionAmount()));

                StatusEffectBruteForce.bruteForce(behaviour, entity, effects, possibleStatusEffects);
            }

            return effects;
        }

        // todo figure out versions where you can query the thing
    }

    public static StatusEffectInstance makeInstance(RegistryEntry<StatusEffect> effect, int amplifier) {
        return new StatusEffectInstance(effect, StatusEffectInstance.INFINITE, amplifier - 1);
    }
}
