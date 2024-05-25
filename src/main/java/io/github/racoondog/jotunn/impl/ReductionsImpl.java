package io.github.racoondog.jotunn.impl;

import io.github.racoondog.jotunn.api.Jotunn;
import io.github.racoondog.jotunn.api.Reductions;
import io.github.racoondog.jotunn.api.configuration.ReductionBehaviour;
import io.github.racoondog.multiversion.MultiVersion;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EntityTypeTags;

public final class ReductionsImpl implements Reductions {
    private final ReductionBehaviour behaviour;

    public ReductionsImpl(ReductionBehaviour behaviour) {
        this.behaviour = behaviour;
    }

    @Override
    public float reduce(float damage, LivingEntity entity, DamageSource damageSource) {
        if (damageSource.isScaledWithDifficulty()) {
            damage = behaviour.reduceByDifficulty(damage, entity, damageSource);
        }

        if (damage <= 0f || entity.isInvulnerableTo(damageSource)) return 0f;

        if (damageSource.isIn(DamageTypeTags.IS_FREEZING) && entity.getType().isIn(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)) {
            damage *= 5f;
        }

        if (damageSource.isIn(DamageTypeTags.DAMAGES_HELMET) && !entity.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
            damage *= 0.75f;
        }

        // Armor reduction
        damage = reduceByArmor(damage, entity, damageSource);

        // Resistance reduction
        damage = reduceByEffects(damage, entity, damageSource);

        // Protection reduction
        damage = reduceByEnchantments(damage, entity, damageSource);

        return Math.max(damage, 0);
    }

    @Override
    public float reduceByArmor(float damage, LivingEntity entity, DamageSource damageSource) {
        if (damageSource.isIn(DamageTypeTags.BYPASSES_ARMOR)) return damage;

        if (MultiVersion.getInstance().isAtLeast(MultiVersion.V1_9)) {
            int armorValue = (int) Jotunn.attributes().getAttributeValue(entity, EntityAttributes.GENERIC_ARMOR);
            float armorToughness = (float) Jotunn.attributes().getAttributeValue(entity, EntityAttributes.GENERIC_ARMOR_TOUGHNESS);

            return DamageUtil.getDamageLeft(damage, damageSource, armorValue, armorToughness);
        } else {
            //noinspection deprecation
            int armorValue = (int) Utils.extractAttributeValue(EntityAttributes.GENERIC_ARMOR, entity.getArmorItems(),
                    stack -> stack.getItem().getAttributeModifiers().modifiers(),
                    modifier -> modifier.attribute() == EntityAttributes.GENERIC_ARMOR,
                    AttributeModifiersComponent.Entry::modifier,true);

            return damage * (1 - armorValue * 0.04f);
        }
    }

    @Override
    public float reduceByEffects(float damage, LivingEntity entity, DamageSource damageSource) {
        if (damageSource.isIn(DamageTypeTags.BYPASSES_EFFECTS) || damageSource.isIn(DamageTypeTags.BYPASSES_RESISTANCE)) return damage;
        if (damageSource.isIn(DamageTypeTags.IS_FIRE) && Jotunn.effects().hasStatusEffect(entity, StatusEffects.FIRE_RESISTANCE)) return damage;

        StatusEffectInstance resistance = Jotunn.effects().getStatusEffect(entity, StatusEffects.RESISTANCE);
        if (resistance == null) return damage;

        int level = resistance.getAmplifier() + 1;
        return Math.max(damage * (1 - level * 0.2f), 0f);
    }

    @Override
    public float reduceByEnchantments(float damage, LivingEntity entity, DamageSource damageSource) {
        if (damageSource.isIn(DamageTypeTags.BYPASSES_EFFECTS) || damageSource.isIn(DamageTypeTags.BYPASSES_ENCHANTMENTS)) return damage;

        int level;
        if (MultiVersion.getInstance().isUnder(MultiVersion.V1_20_5)) {
            level = EnchantmentHelper.getProtectionAmount(entity.getArmorItems(), damageSource);
        } else {
            level = EnchantmentHelper.getProtectionAmount(entity.getAllArmorItems(), damageSource);
        }

        return level > 0 ? DamageUtil.getInflictedDamage(damage, level) : damage;
    }
}
