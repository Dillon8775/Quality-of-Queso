package net.dillon.qualityofqueso.screen.gui.button;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.function.Supplier;

import static net.dillon.qualityofqueso.util.AccessorUtil.key;
import static net.dillon.qualityofqueso.util.ModUtil.options;

public class SortButton extends TransferButton {

    public SortButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, int x, int y, String buttonName, OnPress onPress, Supplier<Boolean> canBeActive) {
        super(screenHandler, font, searchFieldText, x, y, buttonName, onPress, canBeActive);
    }

    @Override
    protected void renderButtonTexture(String id, boolean transferable, AbstractWidget buttonReference, GuiGraphics
            context) {
        ButtonUtil.drawButtonTexture(context, id, this);
        if (Minecraft.getInstance().hasControlDown()) {
            if (options().management.showButtonShortcuts && this.buttonName.equals("sort") && key(ModKeybinds.SORT_CONTAINER) == ModKeybinds.SORT_CONTAINER.getDefaultKey()) {
                ButtonUtil.drawButtonTexture(context, "shortcut/sort_button_shortcut_key", this);
            }
        }
    }
}