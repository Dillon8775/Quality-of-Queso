package net.dillon.qualityofqueso.debug;

import net.minecraft.client.gui.components.debug.DebugGroups;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

import static net.dillon.dillonlib.task.ClientTasks.getLocalPlayer;
import static net.dillon.dillonlib.util.Arithmetics.roundToTenths;

/**
 * A simple hud entry to display the player's coordinates. Just the coordinates alone.
 */
public class SimpleCoordinatesDebugEntry extends ModDebugEntry {

    @Override
    public void display(DebugScreenDisplayer lines, @Nullable Level serverOrClientLevel, @Nullable LevelChunk levelChunk, @Nullable LevelChunk levelChunk1) {
        // Return out if the player isn't in a world, or the player is null
        if (serverOrClientLevel == null) {
            return;
        }

        LocalPlayer localPlayer = getLocalPlayer();
        if (localPlayer == null) {
            return;
        }

        // Add the simple cords line
        lines.addFactToGroup(
                DebugGroups.POSITION,
                "XYZ",
                fact -> fact
                        .formattedValue("%.1f", roundToTenths(localPlayer.getX()))
                        .text(" / ")
                        .formattedValue("%.1f", roundToTenths(localPlayer.getY()))
                        .text(" / ")
                        .formattedValue("%.1f", roundToTenths(localPlayer.getZ()))
        );
    }
}