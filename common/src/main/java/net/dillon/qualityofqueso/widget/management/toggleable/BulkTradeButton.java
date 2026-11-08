package net.dillon.qualityofqueso.widget.management.toggleable;

import net.dillon.qualityofqueso.widget.management.ManagementParams;
import net.dillon.qualityofqueso.widget.management.ManagementSprites;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.updateClient;
import static net.dillon.qualityofqueso.widget.management.ManagementSprites.managementIdentifier;

/**
 * A management button that recursively trades with a villager on the selected trade until its stock runs out or the player runs out of items.
 */
public class BulkTradeButton extends ToggleableButton {

    public BulkTradeButton(ManagementParams params) {
        super(params, b -> updateClient(client -> client.management().bulkTrade = !client.management().bulkTrade));
    }

    @Override
    protected boolean toggle() {
        return client().management().bulkTrade;
    }

    @Override
    protected ManagementSprites getManagementSprites() {
        return new ManagementSprites(
                managementIdentifier("bulk_trade/bulk_trade"),
                managementIdentifier("bulk_trade/normal_trade")
        );
    }

    @Override
    protected Component getDefaultTooltip() {
        return Component.translatable(
                this.toggle()
                        ? "qualityofqueso.gui.trade_all"
                        : "qualityofqueso.gui.trade_normal"
        );
    }
}