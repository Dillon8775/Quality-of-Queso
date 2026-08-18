package net.dillon.qualityofqueso.debug;

import net.dillon.dillonlib.mixin.accessor.DebugScreenEntriesInvoker;
import net.minecraft.client.gui.components.debug.DebugEntryCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import static net.dillon.qualityofqueso.helper.ModHelper.qoqIdentifier;

/**
 * All debug hud entries.
 */
public class ModDebugScreenEntries {
    public static final DebugEntryCategory QOQ = new DebugEntryCategory(Component.translatable("qualityofqueso.title"), 3.0F);
    public static final Identifier REAL_TIME = qoqIdentifier("time");
    public static final Identifier WORLD_TIME = qoqIdentifier("world_time");
    public static final Identifier PLAYER_POS = qoqIdentifier("player_pos");

    /**
     * Registers all Quality of Queso debug entries.
     */
    public static void registerDebugEntries() {
        DebugScreenEntriesInvoker.invokeRegister(qoqIdentifier("date"), new ModDebugEntryDate());
        DebugScreenEntriesInvoker.invokeRegister(qoqIdentifier("real_life_time"), new ModDebugEntryRealLifeTime());

        DebugScreenEntriesInvoker.invokeRegister(qoqIdentifier("in_game_time"), new ModDebugEntryInGameTime());
        DebugScreenEntriesInvoker.invokeRegister(qoqIdentifier("visual_time"), new ModDebugEntryVisualTime());

        DebugScreenEntriesInvoker.invokeRegister(qoqIdentifier("simple_coordinates"), new ModDebugEntrySimpleCoordinates());
        DebugScreenEntriesInvoker.invokeRegister(qoqIdentifier("fastest_flight"), new ModDebugEntryFastestFlight());
        DebugScreenEntriesInvoker.invokeRegister(qoqIdentifier("accurate_facing"), new ModDebugEntryAccurateFacing());
    }
}