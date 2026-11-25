package net.dillon.qualityofqueso.debug;

import net.dillon.dillonlib.util.TickValue;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

import static net.dillon.dillonlib.task.ClientTasks.getLocalPlayer;
import static net.dillon.qualityofqueso.helper.ModConstants.INFINITY_SYMBOL;

/**
 * Displays all the player's current status effects.
 */
public class PotionEffectsDebugEntry extends ModDebugEntry {

    @Override
    public void display(DebugScreenDisplayer displayer, @Nullable Level serverOrClientLevel, @Nullable LevelChunk clientChunk, @Nullable LevelChunk serverChunk) {
        // Return out if level is null
        if (serverChunk == null) {
            return;
        }

        // Get the current player
        LocalPlayer localPlayer = getLocalPlayer();
        if (localPlayer == null) {
            return;
        }

        // Add all effects to the group
        boolean addedBeaconHeader = false;
        for (MobEffectInstance mobEffectInstance : localPlayer.getActiveEffects()) {
            boolean ambient = mobEffectInstance.isAmbient();
            int duration = mobEffectInstance.getDuration();
            int amplifier = mobEffectInstance.getAmplifier() + 1;

            String amplifierText = " " + Component.translatable("enchantment.level." + amplifier).getString();
            if (amplifier - 1 < 1) {
                amplifierText = "";
            }

            String effectName = Component.translatable(mobEffectInstance.getDescriptionId()).getString() + amplifierText;

            if (ambient) {
                if (!addedBeaconHeader) {
                    displayer.addToGroup(ModDebugScreenEntries.POTION_EFFECTS_GROUP, "From Beacon:");
                    addedBeaconHeader = true;
                }
                displayer.addToGroup(ModDebugScreenEntries.POTION_EFFECTS_GROUP, effectName);
            } else {
                displayer.addFactToGroup(
                        ModDebugScreenEntries.POTION_EFFECTS_GROUP,
                        effectName,
                        fact -> fact.value(
                                duration == -1
                                        ? INFINITY_SYMBOL
                                        : new TickValue(mobEffectInstance.getDuration()).formattedFromTick()
                        )
                );
            }
        }
    }
}