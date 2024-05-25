package io.github.racoondog.jotunn.impl.mixin.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.EntityEffectParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(EntityEffectParticleEffect.class)
public interface EntityEffectParticleEffectAccessor {
    @Accessor("color")
    int jotunn$getColor();
}
