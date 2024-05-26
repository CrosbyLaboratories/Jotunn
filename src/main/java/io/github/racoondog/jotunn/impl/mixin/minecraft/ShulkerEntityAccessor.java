package io.github.racoondog.jotunn.impl.mixin.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.ShulkerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(ShulkerEntity.class)
public interface ShulkerEntityAccessor {
    @Accessor("PEEK_AMOUNT")
    static TrackedData<Byte> jotunn$getPeekAmount() {
        throw new AssertionError();
    }

    @Accessor("COVERED_ARMOR_BONUS")
    static EntityAttributeModifier jotunn$getCoveredArmorBonus() {
        throw new AssertionError();
    }
}
