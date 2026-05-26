package net.dillon.qualityofqueso.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;

/**
 * Always quicks moves items when toggled on.
 */
public class AlwaysQuickMoveButton extends ToggleableButton {

    public AlwaysQuickMoveButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, String buttonName, OnPress onPress) {
        super(screenHandler, font, searchFieldText, buttonName, onPress);
    }

    @Override
    protected String onTextureId() {
        return "quick_move/always_quick_move";
    }

    @Override
    protected String offTextureId() {
        return "quick_move/shift_to_quick_move";
    }

    @Override
    protected boolean option() {
        return clientOptionsInstance().getAlwaysQuickMove();
    }

    @Override
    protected Component getTooltipToRender() {
        return Component.translatable(
                this.option()
                        ? "qualityofqueso.gui.always_quick_move"
                        : "qualityofqueso.gui.shift_to_quick_move"
        );
    }
}