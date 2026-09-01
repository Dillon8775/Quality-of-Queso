package net.dillon.qualityofqueso.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

import static net.dillon.dillonlib.util.Arithmetics.roundToTenths;

/**
 * A simple hud entry to display the player's coordinates. Just the coordinates alone.
 */
public class ModDebugEntrySimpleCoordinates extends ModDebugEntry {

    @Override
    public void display(@Nullable DebugScreenDisplayer lines, @Nullable Level level, @Nullable LevelChunk levelChunk, @Nullable LevelChunk levelChunk1) {
        // Return out if the player isn't in a world, or the player is null
        if (level == null) {
            return;
        }
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) {
            return;
        }

        // Adds just the player's coordinates to the hud
        String cords = String.format("XYZ: %s / %s / %s", roundToTenths(localPlayer.getX()), roundToTenths(localPlayer.getY()), roundToTenths(localPlayer.getZ()));
        lines.addToGroup(ModDebugScreenEntries.PLAYER_POS, cords);
    }
}