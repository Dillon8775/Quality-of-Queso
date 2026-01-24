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
 * The sorting button.
 */
public class SortButton extends TransferButton {

    public SortButton(ScreenHandler screenHandler, TextRenderer textRenderer, String searchFieldText, int x, int y, String buttonName, PressAction onPress, Supplier<Boolean> canBeActive) {
        super(screenHandler, textRenderer, searchFieldText, x, y, buttonName, onPress, canBeActive);
    }

    @Override
    protected void renderButtonTexture(String id, boolean transferable, ClickableWidget buttonReference, DrawContext
            context) {
        ButtonUtil.drawButtonTexture(context, id, this);
        if (MinecraftClient.getInstance().isCtrlPressed()) {
            if (options().showButtonShortcuts && this.buttonName.equals("sort") && ModKeybinds.SORT_CONTAINER.boundKey == ModKeybinds.SORT_CONTAINER.getDefaultKey()) {
                ButtonUtil.drawButtonTexture(context, "sort_button_shortcut_key", this);
            }
        }
    }
}