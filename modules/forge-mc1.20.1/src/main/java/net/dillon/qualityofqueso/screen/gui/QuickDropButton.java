package net.dillon.qualityofqueso.screen.gui;

import net.dillon.qualityofqueso.util.ButtonUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.function.Supplier;

/**
 * Representation of the quick drop button.
 */
public class QuickDropButton extends TransferButton {

    public QuickDropButton(AbstractContainerMenu screenHandler, Font font, Supplier<String> searchFieldText, int x, int y, String buttonName, OnPress onPress, Supplier<Boolean> canBeActive) {
        super(screenHandler, font, searchFieldText, x, y, buttonName, onPress, canBeActive);
    }

    /**
     * Removes the "with stack" option, since it's a vanilla feature we can't work around (for now).
     */
    @Override
    protected void renderButtonTexture(String id, int mouseX, int mouseY, boolean transferable, AbstractWidget buttonReference, GuiGraphics
            context) {
        String transferableString = this.searchFieldText.get().startsWith("!") ?
                "_exclude.png" : this.searchFieldText.get().startsWith("#") ?
                "_with_tag.png" : this.searchFieldText.get().startsWith(":") ?
                "_match.png" : ".png";
        String appended = transferable ? transferableString : ".png";
        context.blit(ResourceLocation.parse("qualityofqueso:textures/gui/" + id + appended), buttonReference.getX() - 1, buttonReference.getY() - 1, 0.0F, 0.0F, 12, 12, 12, 12);
        boolean shortcutKeyPressed = Screen.hasControlDown() && Screen.hasAltDown();
        if (shortcutKeyPressed) {
            ButtonUtil.drawButtonTexture(context, "quick_drop_outline", this);
        }
        if (Screen.hasShiftDown() && (this.isMouseOver(mouseX, mouseY) || shortcutKeyPressed)) {
            ButtonUtil.drawButtonTexture(context, "quick_drop_one", this);
        }
    }
}