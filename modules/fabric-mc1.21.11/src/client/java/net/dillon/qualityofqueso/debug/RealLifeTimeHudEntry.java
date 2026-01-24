package net.dillon.qualityofqueso.debug;

import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * A debug hud to display real-life time.
 */
public class RealLifeTimeHudEntry implements DebugHudEntry {

    @Override
    public void render(DebugHudLines lines, @Nullable World world, @Nullable WorldChunk clientChunk, @Nullable WorldChunk chunk) {
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        lines.addLine(time.format(formatter));
    }

    /**
     * Set to {@code true} by default.
     */
    @Override
    public boolean canShow(boolean reducedDebugInfo) {
        return true;
    }
}