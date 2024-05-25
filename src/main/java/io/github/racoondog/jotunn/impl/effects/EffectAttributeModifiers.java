package io.github.racoondog.jotunn.impl.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Map;
import java.util.UUID;

public final class EffectAttributeModifiers {
    private static final EffectAttributeModifier SPEED = new EffectAttributeModifier(StatusEffects.SPEED, EntityAttributes.GENERIC_MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", 0.2f);
    private static final EffectAttributeModifier SLOWNESS = new EffectAttributeModifier(StatusEffects.SLOWNESS, EntityAttributes.GENERIC_MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.15f);
    private static final EffectAttributeModifier HASTE = new EffectAttributeModifier(StatusEffects.HASTE, EntityAttributes.GENERIC_ATTACK_SPEED, "AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3", 0.1f);
    private static final EffectAttributeModifier MINING_FATIGUE = new EffectAttributeModifier(StatusEffects.MINING_FATIGUE, EntityAttributes.GENERIC_ATTACK_SPEED, "55FCED67-E92A-486E-9800-B47F202C4386", -0.1f);
    private static final EffectAttributeModifier HEALTH_BOOST = new EffectAttributeModifier(StatusEffects.HEALTH_BOOST, EntityAttributes.GENERIC_MAX_HEALTH, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4f);
    private static final EffectAttributeModifier LUCK = new EffectAttributeModifier(StatusEffects.LUCK, EntityAttributes.GENERIC_LUCK, "03C3C89D-7037-4B42-869F-B146BCB64D2E", 1f);
    private static final EffectAttributeModifier UNLUCK = new EffectAttributeModifier(StatusEffects.UNLUCK, EntityAttributes.GENERIC_LUCK, "CC5AF142-2BD2-4215-B636-2605AED11727", -1f);

    private static final EffectAttributeModifier[] ALL = new EffectAttributeModifier[]{SPEED, SLOWNESS, HASTE, MINING_FATIGUE, HEALTH_BOOST, LUCK, UNLUCK};
    private static final EffectAttributeModifier[] BEACON = new EffectAttributeModifier[]{SPEED, HASTE};

    public static void getFromAttributes(Map<RegistryEntry<StatusEffect>, StatusEffectInstance> effects, LivingEntity entity, boolean beacon) {
        EffectAttributeModifier[] applicableModifiers = beacon ? BEACON : ALL;

        AttributeContainer attributes = entity.getAttributes(); // effect attributes are always synchronized
        for (EffectAttributeModifier modifier : applicableModifiers) {
            if (attributes.hasModifierForAttribute(modifier.attribute(), modifier.id())) {
                double value = attributes.getModifierValue(modifier.attribute(), modifier.id());
                int amplifier = (int) Math.round(value / modifier.value());
                effects.put(modifier.effect(), StatusEffectsImpl.makeInstance(modifier.effect(), amplifier));
            }
        }
    }

    private record EffectAttributeModifier(RegistryEntry<StatusEffect> effect, RegistryEntry<EntityAttribute> attribute, UUID id, float value) {
        private EffectAttributeModifier(RegistryEntry<StatusEffect> effect, RegistryEntry<EntityAttribute> attribute, String id, float value) {
            this(effect, attribute, UUID.fromString(id), value);
        }
    }
}
