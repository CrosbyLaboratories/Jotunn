package io.github.racoondog.jotunn.api.configuration;

import io.github.racoondog.jotunn.api.effects.StatusEffectEntry;
import org.jetbrains.annotations.Range;

import java.util.Collection;

public interface StatusEffectBehaviour {
    /**
     * @return all {@link StatusEffectEntry} entries, except those returned by {@link #getBeaconEntries()} and
     * those returned by {@link #handleAbsorption(float)}.
     * @since 0.1.0
     */
    Collection<StatusEffectEntry> getEntries();

    /**
     * @return {@link StatusEffectEntry} entries that can be gotten through beacons.
     * @since 0.1.0
     */
    Collection<StatusEffectEntry> getBeaconEntries();

    /**
     * @param absorptionHealth the amount of absorption health on a player
     * @return {@link StatusEffectEntry} entries that corresponds solely to the amount of absorption
     * health on a player. For example, 16 absorption health can only be gotten from eating an enchanted golden apple,
     * so it corresponds to absorption 4. However, 8 absorption health can be gotten from both popping a totem of
     * undying and eating an enchanted golden apple, then taking damage. In that case, this method would return both
     * absorption 2 and absorption 4.
     * @see io.github.racoondog.jotunn.api.configuration.defaults.DefaultStatusEffectBehaviour#handleAbsorption(float)
     * @since 0.1.0
     */
    Collection<StatusEffectEntry> handleAbsorption(float absorptionHealth);

    /**
     * In 1.20.4 and under, the only way to get the active status effects of a player is by running expensive
     * computations on the particle effect colour emanating from them.
     *
     * @return whether brute forcing is enabled.
     * @since 0.1.0
     */
    default boolean bruteForceEnabled() {
        return true;
    }

    /**
     * @return the maximum amount of concurrent status effects on a player that can be computed with the brute force.
     * For instance, if a player has 4 status effects on at once and the max depth is set to 3, the brute force will not
     * return any status effects for that player. However, higher numbers will cause more lag.
     * @since 0.1.0
     */
    default @Range(from = 2, to = Integer.MAX_VALUE) int bruteForceMaxDepth() {
        return 4;
    }
}
