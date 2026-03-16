package net.dillon.qualityofqueso.screen.gui.button;

import net.minecraft.client.gui.Font;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * A button to clear all excluded slots on the screen.
 */
public class ClearExcludedSlotsButton extends TransferButton {

    public ClearExcludedSlotsButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, int x, int y, String buttonName, OnPress onPress) {
        super(screenHandler, font, searchFieldText, x, y, buttonName, false, onPress);
    }

    @Override
    public void playDownSound(SoundManager manager) {
        playButtonClickSound(manager);
    }
}