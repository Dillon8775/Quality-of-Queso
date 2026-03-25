package net.dillon.qualityofqueso.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

/**
 * Displays your facing direction, accurately (ex. northwest, northeast, southwest, etc.)
 */
public class AccurateFacingHudEntry extends QoQScreenEntry {

    @Override
    public void display(DebugScreenDisplayer displayer, @Nullable Level serverOrClientLevel, @Nullable LevelChunk clientChunk, @Nullable LevelChunk serverChunk) {
        Minecraft minecraft = Minecraft.getInstance();
        Entity entity = minecraft.getCameraEntity();
        if (entity == null) {
            return;
        }

        int northA = -110;
        int northB = -160;
        int southA = -70;
        int southB = -20;

        int northAAbs = Math.abs(northA);
        int northBAbs = Math.abs(northB);
        int southAAbs = Math.abs(southA);
        int southBAbs = Math.abs(southB);

        String facing;
        float yaw = Mth.wrapDegrees(entity.getYRot());
        if (yaw > northAAbs && yaw < northBAbs) {
            facing = "northwest";
        } else if (yaw > southBAbs && yaw < southAAbs) {
            facing = "southwest";
        } else if (yaw > southA && yaw < southB) {
            facing = "southeast";
        } else if (yaw < northA && yaw > northB) {
            facing = "northeast";
        } else {
            facing = switch (entity.getDirection()) {
                case EAST -> "east";
                case WEST -> "west";
                case SOUTH -> "south";
                default -> "north";
            };
        }

        displayer.addLine("Facing: " + facing);
    }
}