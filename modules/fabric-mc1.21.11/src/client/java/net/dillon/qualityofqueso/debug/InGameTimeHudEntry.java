package net.dillon.qualityofqueso.debug;

import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

/**
 * A debug hud to display Minecraft's in-game time.
 */
public class InGameTimeHudEntry implements DebugHudEntry {

    @Override
    public void render(DebugHudLines lines, @Nullable World world, @Nullable WorldChunk clientChunk, @Nullable WorldChunk chunk) {
        if (world == null) {
            return;
        }
        long time = world.getTimeOfDay() % 24000;

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
    public boolean canShow(boolean reducedDebugInfo) {
        return true;
    }
}