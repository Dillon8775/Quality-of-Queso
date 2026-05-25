package net.dillon.qualityofqueso.widget;

import net.dillon.qualityofqueso.helper.ButtonHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.function.Supplier;

import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;

/**
 * Sorts items in a container, by tag -> alphabetically, or just alphabetically.
 */
public class SortButton extends QuesoButton {

    public SortButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, String resourceLocation, String buttonName, OnPress onPress, Supplier<Boolean> canBeActive) {
        super(screenHandler, font, searchFieldText, resourceLocation, buttonName, false, onPress, canBeActive);
    }

    @Override
    protected Component getTooltipToRender() {
        return Component.translatable("qualityofqueso.gui." + this.buttonName + "_button", clientOptionsInstance().getSortingOptions().currentSortingMode.getTooltip());
    }

    @Override
    protected void renderBaseButtonTexture(String id, AbstractWidget buttonReference, GuiGraphics graphics) {
        this.renderBaseTexture(graphics);
        ButtonHelper.drawButtonTexture(graphics, id + (!this.canBeActive.get() ? "" : clientOptionsInstance().getSortingOptions().currentSortingMode.getAppended()), this);
        this.renderHoveredTexture(graphics);
    }
}