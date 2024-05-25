package io.github.racoondog.jotunn.impl.mixin.minecraft;

import io.github.racoondog.jotunn.impl.mixin.IAttributeContainer;
import io.github.racoondog.jotunn.impl.mixin.IEntityAttributeInstance;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Set;

@Mixin(AttributeContainer.class)
public abstract class AttributeContainerMixin implements IAttributeContainer {
    @Shadow @Final private Map<RegistryEntry<EntityAttribute>, EntityAttributeInstance> custom;

    @Shadow @Final private Set<EntityAttributeInstance> tracked;

    @Override
    public void jotunn$copyFrom(AttributeContainer other) {
        for (var otherInstance : ((AttributeContainerMixin) (Object) other).custom.values()) {
            @Nullable EntityAttributeInstance instance = custom.get(otherInstance.getAttribute());
            if (instance != null) {
                ((IEntityAttributeInstance) instance).jotunn$copyFrom(otherInstance);
            } else {
                custom.put(otherInstance.getAttribute(), otherInstance);
                if (otherInstance.getAttribute().value().isTracked()) tracked.add(otherInstance);
            }
        }
    }
}
