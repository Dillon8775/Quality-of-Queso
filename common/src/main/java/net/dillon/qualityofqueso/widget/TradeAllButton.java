package net.dillon.qualityofqueso.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static net.dillon.qualityofqueso.helper.ModHelper.options;

/**
 * A button which trades all supplies with a villager.
 */
public class TradeAllButton extends ToggleableButton {

    public TradeAllButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, String buttonName, OnPress onPress) {
        super(screenHandler, font, searchFieldText, buttonName, onPress);
    }

    @Override
    protected String onTextureId() {
        return "trade_all/trade_all";
    }

    @Override
    protected String offTextureId() {
        return "trade_all/trade_normal";
    }

    @Override
    protected boolean option() {
        return options().management.tradeAll;
    }

    @Override
    protected Component getTooltipToRender() {
        return Component.translatable(
                this.option()
                        ? "qualityofqueso.gui.trade_all"
                        : "qualityofqueso.gui.trade_normal"
        );
    }
}