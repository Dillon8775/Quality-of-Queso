package net.dillon.qualityofqueso.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugEntryCategory;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

/**
 * A simple hud entry to display the player's coordinates. Just the coordinates alone.
 */
public class SimpleCoordinatesHudEntry implements DebugScreenEntry {

    @Override
    public void display(@Nullable DebugScreenDisplayer lines, @Nullable Level level, @Nullable LevelChunk levelChunk, @Nullable LevelChunk levelChunk1) {
        if (level == null) {
            return;
        }
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) {
            return;
        }

        String cords = String.format("XYZ: %s / %s / %s", round(localPlayer.getX()), round(localPlayer.getY()), round(localPlayer.getZ()));
        lines.addPriorityLine(cords);
    }

    /**
     * Rounds the cords to the nearest tenths place.
     */
    private static double round(double pos) {
        return Math.round(pos * 10.0) / 10.0;
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