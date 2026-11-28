package net.dillon.qualityofqueso.debug;

import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

import static net.dillon.dillonlib.task.ClientTasks.getMinecraft;
import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;

/**
 * Displays your fps, and only your fps.
 */
public class SimpleFpsDebugEntry extends ModDebugEntry {

    @Override
    public void display(DebugScreenDisplayer displayer, @Nullable Level serverOrClientLevel, @Nullable LevelChunk clientChunk, @Nullable LevelChunk serverChunk) {
        // Return out if mod disabled
        if (!modEnabled()) {
            return;
        }

        // Display simple framerate
        displayer.addPriorityLine(
                String.format(
                        Locale.ROOT,
                        "%d fps",
                        getMinecraft().getFps()
                )
        );
    }
}