package net.dillon.qualityofqueso.debug;

import net.dillon.qualityofqueso.util.AccessorUtil;
import net.minecraft.resources.Identifier;

/**
 * All debug hud entries.
 */
public class ModHudEntries {

    /**
     * Registers all debug hud entries.
     */
    public static void registerDebugEntries() {
        AccessorUtil.registerDebugEntry(Identifier.fromNamespaceAndPath("qualityofqueso", "real_life_time"), new RealLifeTimeHudEntry());
        AccessorUtil.registerDebugEntry(Identifier.fromNamespaceAndPath("qualityofqueso", "in_game_time"), new InGameTimeHudEntry());
    }
}