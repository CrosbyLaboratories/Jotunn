package io.github.racoondog.jotunn.impl.attributes;

import io.github.racoondog.jotunn.api.Attributes;
import io.github.racoondog.jotunn.api.Jotunn;
import io.github.racoondog.jotunn.impl.mixin.IAttributeContainer;
import io.github.racoondog.jotunn.impl.mixin.IEntityAttributeInstance;
import io.github.racoondog.jotunn.impl.mixin.minecraft.ShulkerEntityAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public final class AttributesImpl implements Attributes {
    public static final AttributesImpl INSTANCE = new AttributesImpl();

    private AttributesImpl() {}

    /**
     * @see LivingEntity#getAttributes()
     */
    @Override
    public AttributeContainer getAttributes(LivingEntity entity) {
        AttributeContainer attributes = new AttributeContainer(VersionedDefaultAttributeRegistry.get(entity));

        // Equipment
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            ItemStack stack = entity.getEquippedStack(equipmentSlot);
            stack.applyAttributeModifiers(equipmentSlot, (attribute, modifier) -> {
                EntityAttributeInstance entityAttributeInstance = attributes.getCustomInstance(attribute);
                if (entityAttributeInstance != null) {
                    entityAttributeInstance.removeModifier(modifier.uuid());
                    entityAttributeInstance.addTemporaryModifier(modifier);
                }
            });
        }

        // Status effects
        for (var statusEffect : Jotunn.effects().getStatusEffects(entity)) {
            statusEffect.getEffectType().value().onApplied(attributes, statusEffect.getAmplifier());
        }

        // Handle special cased entities
        handleSpecialCases(entity, attributes::getCustomInstance);

        // Copy tracked attributes
        ((IAttributeContainer) attributes).jotunn$copyFrom(entity.getAttributes());

        return attributes;
    }

    /**
     * @see LivingEntity#getAttributeInstance(RegistryEntry)
     */
    @Override
    public EntityAttributeInstance getAttributeInstance(LivingEntity entity, RegistryEntry<EntityAttribute> attribute) {
        double baseValue = VersionedDefaultAttributeRegistry.get(entity).getBaseValue(attribute);
        EntityAttributeInstance attributeInstance = new EntityAttributeInstance(attribute, o -> {});
        attributeInstance.setBaseValue(baseValue);

        // pre-allocate
        BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> attributeModifierConsumer = (attributeEntry, modifier) -> {
            if (attributeEntry.equals(attribute)) {
                attributeInstance.removeModifier(modifier.uuid());
                attributeInstance.addTemporaryModifier(modifier);
            }
        };

        // Equipment
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            ItemStack stack = entity.getEquippedStack(equipmentSlot);
            stack.applyAttributeModifiers(equipmentSlot, attributeModifierConsumer);
        }

        // Status effects
        for (StatusEffectInstance statusEffect : Jotunn.effects().getStatusEffects(entity)) {
            statusEffect.getEffectType().value().forEachAttributeModifier(statusEffect.getAmplifier(), attributeModifierConsumer);
        }

        // Handle special cased entities
        handleSpecialCases(entity, someAttribute -> someAttribute == attribute ? attributeInstance : null);

        // Copy tracked attributes
        EntityAttributeInstance trackedInstance = entity.getAttributeInstance(attribute);
        if (trackedInstance != null) ((IEntityAttributeInstance) attributeInstance).jotunn$copyFrom(trackedInstance);

        return attributeInstance;
    }

    /**
     * @see LivingEntity#getAttributeValue(RegistryEntry)
     */
    @Override
    public double getAttributeValue(LivingEntity entity, RegistryEntry<EntityAttribute> attribute) {
        return getAttributeInstance(entity, attribute).getValue();
    }

    @Override
    public double getAttributeBaseValue(LivingEntity entity, RegistryEntry<EntityAttribute> attribute) {
        return VersionedDefaultAttributeRegistry.get(entity).getBaseValue(attribute);
    }

    private static void handleSpecialCases(LivingEntity entity, Function<RegistryEntry<EntityAttribute>, EntityAttributeInstance> consumer) {
        if (entity instanceof ShulkerEntity shulkerEntity) {
            if (shulkerEntity.getDataTracker().get(ShulkerEntityAccessor.jotunn$getPeekAmount()) == 0) {
                @Nullable EntityAttributeInstance attributeInstance = consumer.apply(EntityAttributes.GENERIC_ARMOR);
                if (attributeInstance != null) attributeInstance.addPersistentModifier(ShulkerEntityAccessor.jotunn$getCoveredArmorBonus());
            }
        }
    }
}
