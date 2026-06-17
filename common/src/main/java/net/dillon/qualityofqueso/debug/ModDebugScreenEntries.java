package net.dillon.qualityofqueso.debug;

import net.dillon.qualityofqueso.helper.MethodHelper;
import net.minecraft.client.gui.components.debug.DebugEntryCategory;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.helper.ModHelper.ofQoQ;

/**
 * All debug hud entries.
 */
public class ModDebugScreenEntries {
    public static final DebugEntryCategory QOQ = new DebugEntryCategory(Component.translatable("qualityofqueso.title"), 3.0F);

    /**
     * Registers all Quality of Queso debug entries.
     */
    public static void registerDebugEntries() {
        MethodHelper.registerDebugEntry(ofQoQ("real_life_time"), new ModDebugEntryRealLifeTime());
        MethodHelper.registerDebugEntry(ofQoQ("in_game_time"), new ModDebugEntryInGameTime());
        MethodHelper.registerDebugEntry(ofQoQ("date"), new ModDebugEntryDate());
        MethodHelper.registerDebugEntry(ofQoQ("simple_coordinates"), new ModDebugEntrySimpleCoordinates());
        MethodHelper.registerDebugEntry(ofQoQ("fastest_flight"), new ModDebugEntryFastestFlight());
        MethodHelper.registerDebugEntry(ofQoQ("accurate_facing"), new ModDebugEntryAccurateFacing());
    }
}