package net.dillon.qualityofqueso.debug;

import net.dillon.qualityofqueso.util.AccessorUtil;
import net.minecraft.client.gui.components.debug.DebugEntryCategory;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.util.ModUtil.ofQoQ;

/**
 * All debug hud entries.
 */
public class ModHudEntries {
    public static final DebugEntryCategory QOQ = new DebugEntryCategory(Component.translatable("qualityofqueso.title"), 3.0F);

    /**
     * Registers all debug hud entries.
     */
    public static void registerDebugEntries() {
        AccessorUtil.registerDebugEntry(ofQoQ("real_life_time"), new RealLifeTimeHudEntry());
        AccessorUtil.registerDebugEntry(ofQoQ("in_game_time"), new InGameTimeHudEntry());
        AccessorUtil.registerDebugEntry(ofQoQ("simple_coordinates"), new SimpleCoordinatesHudEntry());
        AccessorUtil.registerDebugEntry(ofQoQ("fastest_flight"), new FastestFlightHudEntry());
        AccessorUtil.registerDebugEntry(ofQoQ("accurate_facing"), new AccurateFacingHudEntry());
    }
}