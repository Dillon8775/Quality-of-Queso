package net.dillon.qualityofqueso.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;

/**
 * Displays your facing direction, accurately (ex. northwest, northeast, southwest, etc.)
 */
public class ModDebugEntryAccurateFacing extends ModDebugEntry {

    @Override
    public void display(DebugScreenDisplayer displayer, @Nullable Level serverOrClientLevel, @Nullable LevelChunk clientChunk, @Nullable LevelChunk serverChunk) {
        Minecraft minecraft = Minecraft.getInstance();

        // Return out of the mod isn't enabled
        if (!modEnabled(minecraft)) {
            return;
        }

        Entity entity = minecraft.getCameraEntity();
        // Ensure entity isn't null
        if (entity == null) {
            return;
        }

        // Create absolute values for directions; we can remove the negative if positive
        int northA = -110;
        int northB = -160;
        int southA = -70;
        int southB = -20;

        int northAAbs = Math.abs(northA);
        int northBAbs = Math.abs(northB);
        int southAAbs = Math.abs(southA);
        int southBAbs = Math.abs(southB);

        // Determine the direction facing
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

        // Add the facing line
        displayer.addLine("Facing: " + facing);
    }
}