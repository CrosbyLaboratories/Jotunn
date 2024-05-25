package io.github.racoondog.jotunn.impl.mixin.minecraft;

import io.github.racoondog.jotunn.impl.mixin.IEntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.UUID;

@Mixin(EntityAttributeInstance.class)
public abstract class EntityAttributeInstanceMixin implements IEntityAttributeInstance {
    @Shadow @Final private Map<UUID, EntityAttributeModifier> idToModifiers;

    @Shadow abstract Map<UUID, EntityAttributeModifier> getModifiers(EntityAttributeModifier.Operation operation);

    @Shadow @Final private Map<UUID, EntityAttributeModifier> persistentModifiers;

    @Override
    public void jotunn$copyFrom(EntityAttributeInstance other) {
        for (EntityAttributeModifier modifier : other.getModifiers()) {
            @Nullable EntityAttributeModifier old = idToModifiers.put(modifier.uuid(), modifier);
            if (old != null) {
                getModifiers(old.operation()).remove(old.uuid());
                persistentModifiers.remove(old.uuid());
            }
            getModifiers(modifier.operation()).put(modifier.uuid(), modifier);
        }
    }
}
