package io.github.racoondog.jotunn.api;

import io.github.racoondog.jotunn.api.configuration.CombatBehaviour;
import io.github.racoondog.jotunn.api.configuration.ReductionBehaviour;
import io.github.racoondog.jotunn.api.configuration.StatusEffectBehaviour;
import io.github.racoondog.jotunn.api.configuration.defaults.DefaultCombatBehaviour;
import io.github.racoondog.jotunn.api.configuration.defaults.DefaultReductionBehaviour;
import io.github.racoondog.jotunn.api.configuration.defaults.DefaultStatusEffectBehaviour;
import io.github.racoondog.jotunn.impl.CombatImpl;
import io.github.racoondog.jotunn.impl.FallingImpl;
import io.github.racoondog.jotunn.impl.ReductionsImpl;
import io.github.racoondog.jotunn.impl.attributes.AttributesImpl;
import io.github.racoondog.jotunn.impl.effects.StatusEffectsImpl;
import io.github.racoondog.jotunn.impl.explosion.ExplosionsImpl;

public interface Jotunn {
    static Explosions explosions() {
        return ExplosionsImpl.INSTANCE;
    }

    static Attributes attributes() {
        return AttributesImpl.INSTANCE;
    }

    static Combat combat() {
        return new CombatImpl(DefaultCombatBehaviour.INSTANCE);
    }

    static Combat combat(CombatBehaviour behaviour) {
        return new CombatImpl(behaviour);
    }

    static Falling falling() {
        return FallingImpl.INSTANCE;
    }

    static StatusEffects effects() {
        return new StatusEffectsImpl(DefaultStatusEffectBehaviour.INSTANCE);
    }

    static StatusEffects effects(StatusEffectBehaviour behaviour) {
        return new StatusEffectsImpl(behaviour);
    }

    static Reductions reductions() {
        return new ReductionsImpl(DefaultReductionBehaviour.INSTANCE);
    }

    static Reductions reductions(ReductionBehaviour behaviour) {
        return new ReductionsImpl(behaviour);
    }
}
