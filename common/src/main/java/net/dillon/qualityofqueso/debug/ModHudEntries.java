package net.dillon.qualityofqueso.debug;

import net.dillon.qualityofqueso.helper.MethodHelper;
import net.minecraft.client.gui.components.debug.DebugEntryCategory;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.helper.ModHelper.ofQoQ;

/**
 * All debug hud entries.
 */
public class ModHudEntries {
    public static final DebugEntryCategory QOQ = new DebugEntryCategory(Component.translatable("qualityofqueso.title"), 3.0F);

    /**
     * Registers all Quality of Queso debug entries.
     */
    public static void registerDebugEntries() {
        MethodHelper.registerDebugEntry(ofQoQ("real_life_time"), new RealLifeTimeHudEntry());
        MethodHelper.registerDebugEntry(ofQoQ("in_game_time"), new InGameTimeHudEntry());
        MethodHelper.registerDebugEntry(ofQoQ("date"), new DateHudEntry());
        MethodHelper.registerDebugEntry(ofQoQ("simple_coordinates"), new SimpleCoordinatesHudEntry());
        MethodHelper.registerDebugEntry(ofQoQ("fastest_flight"), new FastestFlightHudEntry());
        MethodHelper.registerDebugEntry(ofQoQ("accurate_facing"), new AccurateFacingHudEntry());
    }
}