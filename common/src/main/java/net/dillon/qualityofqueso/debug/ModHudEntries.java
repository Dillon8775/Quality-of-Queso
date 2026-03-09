package net.dillon.qualityofqueso.debug;

import net.dillon.qualityofqueso.util.AccessorUtil;

import static net.dillon.qualityofqueso.util.ModUtil.ofQoQ;

/**
 * All debug hud entries.
 */
public class ModHudEntries {

    /**
     * Registers all debug hud entries.
     */
    public static void registerDebugEntries() {
        AccessorUtil.registerDebugEntry(ofQoQ("real_life_time"), new RealLifeTimeHudEntry());
        AccessorUtil.registerDebugEntry(ofQoQ("in_game_time"), new InGameTimeHudEntry());
    }
}