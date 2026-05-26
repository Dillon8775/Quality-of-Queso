package net.dillon.qualityofqueso.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;

/**
 * A separate class for the include hotbar button.
 */
public class IncludeHotbarButton extends ToggleableButton {

    public IncludeHotbarButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, String buttonName, OnPress onPress) {
        super(screenHandler, font, searchFieldText, buttonName, onPress);
    }

    @Override
    protected String onTextureId() {
        return "hotbar/including_hotbar";
    }

    @Override
    protected String offTextureId() {
        return "hotbar/excluding_hotbar";
    }

    @Override
    protected boolean option() {
        return clientOptionsInstance().getManagementOptions().includingHotbar;
    }

    @Override
    protected Component getTooltipToRender() {
        return Component.translatable(
                this.option()
                        ? "qualityofqueso.gui.include_hotbar"
                        : "qualityofqueso.gui.exclude_hotbar"
        );
    }
}