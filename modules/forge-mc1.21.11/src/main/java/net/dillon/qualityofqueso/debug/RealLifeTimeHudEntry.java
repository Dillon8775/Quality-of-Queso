package net.dillon.qualityofqueso.debug;

import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * A debug hud to display real-life time.
 */
public class RealLifeTimeHudEntry implements DebugScreenEntry {

    @Override
    public void display(@NonNull DebugScreenDisplayer lines, @Nullable Level level, @Nullable LevelChunk clientChunk, @Nullable LevelChunk chunk) {
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        lines.addLine(time.format(formatter));
    }

    /**
     * Set to {@code true} by default.
     */
    @Override
    public boolean isAllowed(boolean reducedDebugInfo) {
        return true;
    }
}