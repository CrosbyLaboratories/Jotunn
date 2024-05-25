package io.github.racoondog.jotunn.api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

@ApiStatus.NonExtendable
public interface StatusEffects {
    Collection<StatusEffectInstance> getStatusEffects(LivingEntity entity);
    Map<RegistryEntry<StatusEffect>, StatusEffectInstance> getActiveStatusEffects(LivingEntity entity);
    boolean hasStatusEffect(LivingEntity entity, RegistryEntry<StatusEffect> effect);
    @Nullable
    StatusEffectInstance getStatusEffect(LivingEntity entity, RegistryEntry<StatusEffect> effect);
}
