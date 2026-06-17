package net.dillon.qualityofqueso.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;

/**
 * Displays the date in-game.
 */
public class ModDebugEntryDate extends ModDebugEntry {

    @Override
    public void display(DebugScreenDisplayer lines, @Nullable Level serverOrClientLevel, @Nullable LevelChunk clientChunk, @Nullable LevelChunk serverChunk) {
        // Return out of the mod isn't enabled
        if (!modEnabled(Minecraft.getInstance())) {
            return;
        }

        // Display the date in-game
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        lines.addLine(date.format(formatter));
    }
}