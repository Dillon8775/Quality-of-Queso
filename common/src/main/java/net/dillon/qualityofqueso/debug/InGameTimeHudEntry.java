package net.dillon.qualityofqueso.debug;

import net.minecraft.client.gui.components.debug.DebugEntryCategory;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import static net.dillon.qualityofqueso.util.ModUtil.ofQoQ;

/**
 * A debug hud to display Minecraft's in-game time.
 */
public class InGameTimeHudEntry implements DebugScreenEntry {

    @Override
    public void display(@NonNull DebugScreenDisplayer lines, @Nullable Level level, @Nullable LevelChunk clientChunk, @Nullable LevelChunk chunk) {
        if (level == null) {
            return;
        }
        long time = level.getOverworldClockTime() % 24000;

        time = (time + 6000) % 24000;

        int hours = (int) (time / 1000);
        int minutes = (int) ((time % 1000) * 60 / 1000);

        String amPm = hours >= 12 ? "PM" : "AM";

        String description;
        if (hours == 0) {
            description = "Midnight";
        } else if (hours >= 20 || (hours >= 1 && hours < 5)) {
            description = "Night";
        } else if (hours == 5) {
            description = "Early Morning";
        } else if (hours >= 6 && hours < 12) {
            description = "Morning";
        } else if (hours == 12) {
            description = "Noon";
        } else if (hours >= 13 && hours < 17) {
            description = "Day";
        } else {
            description = "Evening";
        }

        hours = hours % 12;
        if (hours == 0) {
            hours = 12;
        }

        String formatted = String.format("%d:%02d %s", hours, minutes, amPm);
        lines.addToGroup(ofQoQ("real_life_time"), description + " (" + formatted + ", in-game)");
    }

    @Override
    public boolean isAllowed(boolean reducedDebugInfo) {
        return true;
    }

    @Override
    public DebugEntryCategory category() {
        return ModHudEntries.QOQ;
    }
}