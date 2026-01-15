package net.dillon.qualityofqueso.screen.gui;

import net.dillon.qualityofqueso.util.ButtonUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.screen.ScreenHandler;

import java.util.function.Supplier;

/**
 * A button which swaps inventories.
 */
public class SwapButton extends TransferButton {

    public SwapButton(ScreenHandler screenHandler, TextRenderer textRenderer, String searchFieldText, int x, int y, String buttonName, PressAction onPress, Supplier<Boolean> canBeActive) {
        super(screenHandler, textRenderer, searchFieldText, x, y, buttonName, onPress, canBeActive);
    }

    @Override
    protected void renderButtonTexture(String id, boolean transferable, ClickableWidget buttonReference, DrawContext
            context) {
        ButtonUtil.drawButtonTexture(context, id, this);
    }
}