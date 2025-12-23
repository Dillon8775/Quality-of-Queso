package net.dillon.qualityofqueso.screen.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.function.Supplier;

/**
 * Representation of the quick drop button.
 */
public class QuickDropButton extends TransferButton {

    public QuickDropButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, int x, int y, String buttonName, OnPress onPress, Supplier<Boolean> canBeActive) {
        super(screenHandler, font, searchFieldText, x, y, buttonName, onPress, canBeActive);
    }

    /**
     * Removes the "with stack" option, since it's a vanilla feature we can't work around (for now).
     */
    @Override
    protected void renderButtonTexture(String id, boolean transferable, AbstractWidget buttonReference, GuiGraphics
            context) {
        String transferableString = this.searchFieldText.startsWith("!") ?
                "_exclude.png" : this.searchFieldText.startsWith("#") ?
                "_with_tag.png" : ".png";
        String appended = transferable ? transferableString : ".png";
        context.blit(ResourceLocation.parse("qualityofqueso:textures/gui/" + id + appended), buttonReference.getX() - 1, buttonReference.getY() - 1, 0.0F, 0.0F, 12, 12, 12, 12);
    }
}