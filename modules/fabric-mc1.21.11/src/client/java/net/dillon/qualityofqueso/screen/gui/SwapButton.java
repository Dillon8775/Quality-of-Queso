package net.dillon.qualityofqueso.screen.gui;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.screen.ScreenHandler;

import java.util.function.Supplier;

import static net.dillon.qualityofqueso.main.QoQ.options;

/**
 * A button which swaps inventories.
 */
public class SwapButton extends TransferButton {

    public SwapButton(ScreenHandler screenHandler, TextRenderer textRenderer, String searchFieldText, int x, int y, String buttonName, PressAction onPress, Supplier<Boolean> canBeActive) {
        super(screenHandler, textRenderer, searchFieldText, x, y, buttonName, onPress, canBeActive);
    }

    /**
     * Removes the "with stack" option, since it's a vanilla feature we can't work around (for now).
     */
    @Override
    protected void renderButtonTexture(String id, boolean transferable, ClickableWidget buttonReference, DrawContext
            context) {
        ButtonUtil.drawButtonTexture(context, id, this);
        if (MinecraftClient.getInstance().isCtrlPressed()) {
            if (options().showButtonOutlines) {
                ButtonUtil.drawButtonTexture(context, "swap_button_outline", this);
            }
            if (options().showButtonShortcuts && options().shortcutKeys && this.buttonName.equals("swap") && ModKeybinds.SWAP_ITEMS.boundKey == ModKeybinds.SWAP_ITEMS.getDefaultKey()) {
                ButtonUtil.drawButtonTexture(context, "swap_button_shortcut_key", this);
            }
        }
    }
}