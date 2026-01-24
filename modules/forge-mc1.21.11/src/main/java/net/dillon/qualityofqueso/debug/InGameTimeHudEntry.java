package net.dillon.qualityofqueso.debug;

import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * A debug hud to display Minecraft's in-game time.
 */
public class InGameTimeHudEntry implements DebugScreenEntry {

    @Override
    public void display(@NonNull DebugScreenDisplayer lines, @Nullable Level level, @Nullable LevelChunk clientChunk, @Nullable LevelChunk chunk) {
        if (level == null) {
            return;
        }
        long time = level.getDayTime() % 24000;

        time = (time + 6000) % 24000;

        int hours = (int) (time / 1000);
        int minutes = (int) ((time % 1000) * 60 / 1000);

        String amPm = hours >= 12 ? "PM" : "AM";
        hours = hours % 12;
        if (hours == 0) hours = 12;

        String formatted = String.format("%d:%02d %s", hours, minutes, amPm);
        lines.addLine(formatted + " (in-game)");
    }

    /**
     * Set to {@code true} by default.
     */
    @Override
    public boolean isAllowed(boolean reducedDebugInfo) {
        return true;
    }
}