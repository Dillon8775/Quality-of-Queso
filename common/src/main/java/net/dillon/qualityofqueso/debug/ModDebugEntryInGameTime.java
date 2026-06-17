package net.dillon.qualityofqueso.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;

/**
 * A debug hud to display Minecraft's in-game time.
 */
public class ModDebugEntryInGameTime extends ModDebugEntry {
    protected static boolean DISPLAYING_IN_GAME_TIME = false;

    @Override
    public void display(@NonNull DebugScreenDisplayer lines, @Nullable Level level, @Nullable LevelChunk clientChunk, @Nullable LevelChunk chunk) {
        // Return out of the mod isn't enabled, or the player is not in a world
        if (!modEnabled(Minecraft.getInstance()) || level == null) {
            DISPLAYING_IN_GAME_TIME = false;
            return;
        }

        // Calculate the time
        long time = level.getOverworldClockTime() % 24000;

        time = (time + 6000) % 24000;

        int hours = (int) (time / 1000);
        int minutes = (int) ((time % 1000) * 60 / 1000);

        // Determine AM or PM
        String amPm = hours >= 12 ? "PM" : "AM";

        // Calculate total minutes
        int totalMinutes = hours * 60 + minutes;

        // Determine time of day
        String description = "Night";
        if (totalMinutes >= 0 && totalMinutes < 60) { // 12:00 AM - 12:59 AM
            description = "Midnight";
        } else if (totalMinutes >= 12 * 60 && totalMinutes < 13 * 60) { // 12:00 PM - 12:59 PM
            description = "Noon";
        } else if (totalMinutes >= 60 && totalMinutes < 270) { // 1:00 AM - 4:30 AM
            description = "Night";
        } else if (totalMinutes >= 270 && totalMinutes < 345) { // 4:30 AM - 5:45 AM
            description = "Sunrise";
        } else if (totalMinutes >= 345 && totalMinutes < 480) { // 5:45 AM - 8:00 AM
            description = "Morning";
        } else if (totalMinutes >= 480 && totalMinutes < 720) { // 8:00 AM - 11:59 AM
            description = "Day";
        } else if (totalMinutes >= 780 && totalMinutes < 1020) { // 1:00 PM - 5:00 PM
            description = "Day";
        } else if (totalMinutes >= 1020 && totalMinutes < 1080) { // 5:00 PM - 6:00 PM
            description = "Evening";
        } else if (totalMinutes >= 1080 && totalMinutes < 1190) { // 6:00 PM - 7:50 PM
            description = "Sunset";
        }

        // Fix hours
        hours = hours % 12;
        if (hours == 0) {
            hours = 12;
        }

        // Add the world's in-game time to the hud
        String formatted = String.format("%d:%02d %s", hours, minutes, amPm);
        lines.addLine(description + " (" + formatted + ", in-game)");
        DISPLAYING_IN_GAME_TIME = true;
    }
}
