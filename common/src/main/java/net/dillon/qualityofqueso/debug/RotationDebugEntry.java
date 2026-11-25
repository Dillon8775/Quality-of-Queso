package net.dillon.qualityofqueso.debug;

import net.minecraft.client.gui.components.debug.DebugGroups;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

import static net.dillon.dillonlib.task.ClientTasks.getMinecraft;
import static net.dillon.dillonlib.util.Arithmetics.roundToTenths;

/**
 * Displays the player's rotation values, which allows for perfection in flying the fastest with firework rockets and an elytra (40 = target).
 */
public class RotationDebugEntry extends ModDebugEntry {

    @Override
    public void display(DebugScreenDisplayer displayer, @Nullable Level serverOrClientLevel, @Nullable LevelChunk clientChunk, @Nullable LevelChunk serverChunk) {
        Entity entity = getMinecraft().getCameraEntity();

        // Ensure our entity is a player
        if (!(entity instanceof LocalPlayer)) {
            return;
        }

        // Add the x-rotation string for the player's fastest flight
        displayer.addFactToGroup(
                DebugGroups.POSITION,
                "Rotation",
                fact -> fact
                        .text("X = ")
                        .formattedValue("%.1f", roundToTenths(Mth.wrapDegrees(entity.getYRot())))
                        .text(" Y = ")
                        .formattedValue("%.1f", roundToTenths(Mth.wrapDegrees(entity.getXRot())))
        );
    }
}