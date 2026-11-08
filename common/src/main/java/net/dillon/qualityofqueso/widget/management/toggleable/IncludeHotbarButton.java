package net.dillon.qualityofqueso.widget.management.toggleable;

import net.dillon.qualityofqueso.widget.management.ManagementParams;
import net.dillon.qualityofqueso.widget.management.ManagementSprites;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.helper.ModHelper.sendClientPreferencesToServer;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.updateClient;
import static net.dillon.qualityofqueso.widget.management.ManagementSprites.managementIdentifier;

/**
 * A management button that includes/excludes the player's hotbar from any management feature, like transferring, quick dropping, sorting, etc.
 */
public class IncludeHotbarButton extends ToggleableButton {

    public IncludeHotbarButton(ManagementParams params) {
        super(params, b -> {
            updateClient(client -> client.management().includingHotbar = !client.management().includingHotbar);
            sendClientPreferencesToServer();
        });
    }

    @Override
    protected boolean toggle() {
        return client().management().includingHotbar;
    }

    @Override
    protected ManagementSprites getManagementSprites() {
        return new ManagementSprites(
                managementIdentifier("hotbar/including_hotbar"),
                managementIdentifier("hotbar/excluding_hotbar")
        );
    }

    @Override
    protected Component getDefaultTooltip() {
        return Component.translatable(
                this.toggle()
                        ? "qualityofqueso.gui.include_hotbar"
                        : "qualityofqueso.gui.exclude_hotbar"
        );
    }
}