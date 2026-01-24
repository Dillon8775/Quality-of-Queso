package net.dillon.qualityofqueso.screen.gui;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.function.Supplier;

import static net.dillon.qualityofqueso.main.QoQ.options;

public class SortButton extends TransferButton {

    public SortButton(AbstractContainerMenu screenHandler, Font font, Supplier<String> searchFieldText, int x, int y, String buttonName, OnPress onPress, Supplier<Boolean> canBeActive) {
        super(screenHandler, font, searchFieldText, x, y, buttonName, onPress, canBeActive);
    }

    @Override
    protected void renderButtonTexture(String id, int mouseX, int mouseY, boolean transferable, AbstractWidget buttonReference, GuiGraphics
            context) {
        ButtonUtil.drawButtonTexture(context, id, this);
        if (Screen.hasControlDown()) {
            if (options().showButtonShortcuts && this.buttonName.equals("sort") && ModKeybinds.SORT_CONTAINER.getKey() == ModKeybinds.SORT_CONTAINER.getDefaultKey()) {
                ButtonUtil.drawButtonTexture(context, "sort_button_shortcut_key", this);
            }
        }
    }
}