package net.dillon.qualityofqueso.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;

/**
 * A debug hud to display real-life time.
 */
public class RealLifeTimeHudEntry extends QoQScreenEntry {

    @Override
    public void display(@NonNull DebugScreenDisplayer lines, @Nullable Level level, @Nullable LevelChunk clientChunk, @Nullable LevelChunk chunk) {
        // Return out of the mod isn't enabled
        if (!modEnabled(Minecraft.getInstance())) {
            return;
        }

        // Display the player's local time in game
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        lines.addLine(time.format(formatter));
    }
}