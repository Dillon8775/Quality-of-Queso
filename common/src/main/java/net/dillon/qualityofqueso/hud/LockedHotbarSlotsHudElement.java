package net.dillon.qualityofqueso.hud;

import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.Set;

import static net.dillon.dillonlib.task.ClientTasks.*;
import static net.dillon.qualityofqueso.helper.GuiHelper.isPositioningElements;
import static net.dillon.qualityofqueso.helper.ModConstants.LOCKED_SLOT_TEXTURE;
import static net.dillon.qualityofqueso.helper.ModHelper.qoqIdentifier;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Holds {@code Locked Hotbar Slots} to extract.
 */
public class LockedHotbarSlotsHudElement extends ModHudElement {

    /**
     * Extracts the locked hotbar slots.
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics) {
        if (!isPositioningElements() && (!client().lockedSlots().lockedSlots || !client().lockedSlots().showLock.inHud() || minecraft.player == null)) {
            return;
        }

        Set<Integer> lockedPlayerSlots = ContainerHelper.getLockedSlots(false);
        boolean positioningElements = isPositioningElements();
        if (!positioningElements && lockedPlayerSlots.isEmpty()) {
            return;
        }

        for (int slot = 0; slot < 9; slot++) {
            if (!lockedPlayerSlots.contains(slot)) {
                if (positioningElements) {
                    this.renderWarningIndicator(graphics, slot, 0, null, client().hud().otherElementsY);
                }
                continue;
            }

            if (!positioningElements && minecraft.player.getInventory().getItem(slot).isEmpty()) {
                continue;
            }

            drawSprite(
                    graphics,
                    qoqIdentifier(LOCKED_SLOT_TEXTURE),
                    getGuiWidth(graphics) - 91 + (slot * 20),
                    (getGuiHeight(graphics) + 11) + client().hud().otherElementsY,
                    10,
                    10
            );
        }
    }
}