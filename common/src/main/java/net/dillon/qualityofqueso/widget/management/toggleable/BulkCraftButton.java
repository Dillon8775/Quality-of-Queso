package net.dillon.qualityofqueso.widget.management.toggleable;

import net.dillon.qualityofqueso.widget.management.ManagementParams;
import net.dillon.qualityofqueso.widget.management.ManagementSprites;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.updateClient;
import static net.dillon.qualityofqueso.widget.management.ManagementSprites.managementIdentifier;

/**
 * A management button that recursively crafts a selected recipe until ingredients run out.
 */
public class BulkCraftButton extends ToggleableButton {

    public BulkCraftButton(ManagementParams params) {
        super(params, b -> updateClient(client -> client.management().bulkCraft = !client.management().bulkCraft));
    }

    @Override
    protected boolean toggle() {
        return client().management().bulkCraft;
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
        return Component.translatable(
                this.toggle()
                        ? "qualityofqueso.gui.bulk_craft"
                        : "qualityofqueso.gui.normal_craft"
        );
    }
}