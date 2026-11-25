package net.dillon.qualityofqueso.debug;

import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * A debug hud to display real-life time.
 */
public class RealLifeTimeDebugEntry extends ModDebugEntry {
    private static String LOCAL_TIME;

    @Override
    public void display(@NonNull DebugScreenDisplayer lines, @Nullable Level serverOrClientLevel, @Nullable LevelChunk clientChunk, @Nullable LevelChunk chunk) {
        // Display the player's local time in game
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");

        LOCAL_TIME = time.format(formatter);

        // Add the real-life time line
        lines.addFactToGroup(
                ModDebugScreenEntries.DATE_AND_TIME_GROUP,
                "Local Time",
                fact -> fact.value(getLocalTime())
        );
    }

    /**
     * @return the current local time.
     */
    public static String getLocalTime() {
        return LOCAL_TIME;
    }
}