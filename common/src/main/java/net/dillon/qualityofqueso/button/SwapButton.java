package net.dillon.qualityofqueso.button;

import net.minecraft.client.gui.Font;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.function.Supplier;

/**
 * A button which swaps inventories.
 */
public class SwapButton extends TransferButton {
    private static final int DEFAULT_SWAP_COOLDOWN = 20;
    public static int SWAP_COOLDOWN = DEFAULT_SWAP_COOLDOWN;

    public SwapButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, String resourceLocation, String buttonName, OnPress onPress, Supplier<Boolean> canBeActive) {
        super(screenHandler, font, searchFieldText, resourceLocation, buttonName, false, onPress, canBeActive);
    }

    /**
     * Resets the swapping cooldown.
     */
    public static void resetCooldown() {
        SWAP_COOLDOWN = DEFAULT_SWAP_COOLDOWN;
    }
}