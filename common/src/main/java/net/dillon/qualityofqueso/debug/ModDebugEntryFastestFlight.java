package net.dillon.qualityofqueso.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

import static net.dillon.dillonlib.util.Arithmetics.roundToTenths;


/**
 * Displays the player'x {@code X-rotation,} which allows for perfection in flying the fastest with firework rockets and an elytra (40 = target).
 */
public class ModDebugEntryFastestFlight extends ModDebugEntry {

    @Override
    public void display(DebugScreenDisplayer displayer, @Nullable Level serverOrClientLevel, @Nullable LevelChunk clientChunk, @Nullable LevelChunk serverChunk) {
        Minecraft minecraft = Minecraft.getInstance();
        Entity entity = minecraft.getCameraEntity();

        // Ensure our entity is a player
        if (!(entity instanceof LocalPlayer player)) {
            return;
        }

        // Do not display this debug entry if the player does not have an elytra equipped
        if (!player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
            return;
        }

        // Add the x-rotation string for the player's fastest flight
        displayer.addToGroup(ModDebugScreenEntries.PLAYER_POS, "Fastest Flight for Elytra (target = 40/-40): " + roundToTenths(Mth.wrapDegrees(entity.getXRot())));
    }
}