package net.dillon.qualityofqueso.util;

import net.dillon.dillonlib.annotation.Dill;
import net.dillon.dillonlib.annotation.DillEnv;

/**
 * Used to track the countdown (in ticks) for the item frame's glow.
 */
@Dill(env = DillEnv.COMMON)
public interface GlowCountdown {
    void startGlowCountdown(int ticks);
}