package net.dillon.qualityofqueso.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static net.dillon.qualityofqueso.helper.ModHelper.options;

/**
 * A button to only fill present stacks in a container.
 */
public class FillStacksButton extends ToggleableButton {

    public FillStacksButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, String buttonName, OnPress onPress) {
        super(screenHandler, font, searchFieldText, buttonName, onPress);
    }

    @Override
    protected String onTextureId() {
        return "fill_stacks/fill_stacks";
    }

    @Override
    protected String offTextureId() {
        return "fill_stacks/fill_all";
    }

    @Override
    protected boolean option() {
        return options().getFillStacks();
    }

    @Override
    protected Component getTooltipToRender() {
        return Component.translatable(
                this.option()
                        ? "qualityofqueso.gui.fill_stacks"
                        : "qualityofqueso.gui.fill_all"
        );
    }
}