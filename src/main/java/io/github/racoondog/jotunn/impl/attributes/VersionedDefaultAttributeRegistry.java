package io.github.racoondog.jotunn.impl.attributes;

import io.github.racoondog.multiversion.MultiVersion;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public final class VersionedDefaultAttributeRegistry {
    private static final Reference2ObjectMap<EntityType<?>, Supplier<DefaultAttributeContainer>> REGISTRY = new Reference2ObjectOpenHashMap<>();

    private VersionedDefaultAttributeRegistry() {}

    @SuppressWarnings("unchecked")
    public static <T extends LivingEntity> DefaultAttributeContainer get(T entity) {
        EntityType<LivingEntity> type = (EntityType<LivingEntity>) entity.getType();
        @Nullable Supplier<DefaultAttributeContainer> versionedOverride = REGISTRY.get(type);
        return versionedOverride == null ? DefaultAttributeRegistry.get(type) : versionedOverride.get();
    }

    static {
        REGISTRY.put(EntityType.WOLF, () -> {
           if (MultiVersion.getInstance().isUnder(MultiVersion.V1_20_5)) {
               return MobEntity.createMobAttributes()
                       .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3F)
                       .add(EntityAttributes.GENERIC_MAX_HEALTH, 8.0)
                       .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0)
                       .build();
           }
           return DefaultAttributeRegistry.get(EntityType.WOLF);
        });
    }
}
