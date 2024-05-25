package io.github.racoondog.jotunn.api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface Attributes {
    AttributeContainer getAttributes(LivingEntity entity);

    EntityAttributeInstance getAttributeInstance(LivingEntity entity, RegistryEntry<EntityAttribute> attribute);

    double getAttributeValue(LivingEntity entity, RegistryEntry<EntityAttribute> attribute);

    double getAttributeBaseValue(LivingEntity entity, RegistryEntry<EntityAttribute> attribute);
}
