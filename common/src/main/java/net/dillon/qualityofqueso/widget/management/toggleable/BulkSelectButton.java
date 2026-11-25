package net.dillon.qualityofqueso.widget.management.toggleable;

import net.dillon.qualityofqueso.widget.management.ManagementParams;
import net.dillon.qualityofqueso.widget.management.ManagementSprites;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.helper.ManagementHelper.isMerchantScreen;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.updateClient;
import static net.dillon.qualityofqueso.widget.management.ManagementSprites.managementIdentifier;

/**
 * A management button that recursively crafts a selected recipe or trade until ingredients run out.
 */
public class BulkSelectButton extends ToggleableButton {

    public BulkSelectButton(ManagementParams params) {
        super(params, b -> updateClient(client -> {
            if (isMerchantScreen()) {
                client.management().bulkTrading = !client.management().bulkTrading;
            } else {
                client.management().bulkCrafting = !client.management().bulkCrafting;
            }
        }));
    }

    @Override
    protected boolean toggle() {
        return isMerchantScreen() ? client().management().bulkTrading : client().management().bulkCrafting;
    }

    @Override
    protected ManagementSprites getManagementSprites() {
        return new ManagementSprites(
                managementIdentifier("bulk_craft/bulk_craft"),
                managementIdentifier("bulk_craft/normal_craft")
        );
    }

    @Override
    protected Component getDefaultTooltip() {
        if (isMerchantScreen()) {
            return Component.translatable(
                    this.toggle()
                            ? "qualityofqueso.gui.trade_all"
                            : "qualityofqueso.gui.trade_normal"
            );
        } else {
            return Component.translatable(
                    this.toggle()
                            ? "qualityofqueso.gui.bulk_craft"
                            : "qualityofqueso.gui.normal_craft"
            );
        }
    }
}