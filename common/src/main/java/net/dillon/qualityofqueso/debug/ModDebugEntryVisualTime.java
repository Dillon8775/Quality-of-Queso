package net.dillon.qualityofqueso.debug;

import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Displays whether {@code Visual Time} is enabled.
 */
public class ModDebugEntryVisualTime extends ModDebugEntry {

    @Override
    public void display(DebugScreenDisplayer lines, @Nullable Level level, @Nullable LevelChunk clientChunk, @Nullable LevelChunk serverChunk) {
        // Return out if visual time is off, or if the player is not in a world
        if (!client().visualTime().overrideClientTime || level == null) {
            return;
        }

        // Add visual time enabled line
        lines.addToGroup(ModDebugScreenEntries.WORLD_TIME, "Visual Time Enabled");
    }
}