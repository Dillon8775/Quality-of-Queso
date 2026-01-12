package net.dillon.qualityofqueso.screen.gui;

import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.function.Supplier;

/**
 * A button which swaps inventories.
 */
public class SwapButton extends TransferButton {

    public SwapButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, int x, int y, String buttonName, OnPress onPress, Supplier<Boolean> canBeActive) {
        super(screenHandler, font, searchFieldText, x, y, buttonName, onPress, canBeActive);
    }

    /**
     * Removes the "with stack" option, since it's a vanilla feature we can't work around (for now).
     */
    @Override
    protected void renderButtonTexture(String id, boolean transferable, AbstractWidget buttonReference, GuiGraphics
            context) {
        ButtonUtil.drawButtonTexture(context, id, this);
        if (Minecraft.getInstance().hasControlDown()) {
            if (ModClientOptions.SHOW_BUTTON_OUTLINES.get()) {
                ButtonUtil.drawButtonTexture(context, "swap_button_outline", this);
            }
            if (ModClientOptions.SHOW_BUTTON_SHORTCUTS.get() && ModClientOptions.SHORTCUT_KEYS.get() && this.buttonName.equals("swap")) {
                ButtonUtil.drawButtonTexture(context, "swap_button_shortcut_key", this);
            }
        }
    }
}