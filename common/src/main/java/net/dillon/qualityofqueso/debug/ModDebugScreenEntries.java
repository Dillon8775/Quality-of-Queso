package net.dillon.qualityofqueso.debug;

import net.dillon.dillonlib.mixin.accessor.DebugScreenEntriesInvoker;
import net.dillon.qualityofqueso.helper.MethodHelper;
import net.minecraft.client.gui.components.debug.DebugEntryCategory;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.helper.ModHelper.qoqIdentifier;

/**
 * All debug hud entries.
 */
public class ModDebugScreenEntries {
    public static final DebugEntryCategory QOQ = new DebugEntryCategory(Component.translatable("qualityofqueso.title"), 3.0F);

    /**
     * Registers all Quality of Queso debug entries.
     */
    public static void registerDebugEntries() {
        DebugScreenEntriesInvoker.invokeRegister(qoqIdentifier("real_life_time"), new ModDebugEntryRealLifeTime());
        DebugScreenEntriesInvoker.invokeRegister(qoqIdentifier("in_game_time"), new ModDebugEntryInGameTime());
        DebugScreenEntriesInvoker.invokeRegister(qoqIdentifier("date"), new ModDebugEntryDate());
        DebugScreenEntriesInvoker.invokeRegister(qoqIdentifier("simple_coordinates"), new ModDebugEntrySimpleCoordinates());
        DebugScreenEntriesInvoker.invokeRegister(qoqIdentifier("fastest_flight"), new ModDebugEntryFastestFlight());
        DebugScreenEntriesInvoker.invokeRegister(qoqIdentifier("accurate_facing"), new ModDebugEntryAccurateFacing());
    }
}