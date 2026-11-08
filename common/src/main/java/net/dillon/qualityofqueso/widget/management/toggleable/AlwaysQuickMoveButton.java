package net.dillon.qualityofqueso.widget.management.toggleable;

import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.widget.management.ManagementParams;
import net.dillon.qualityofqueso.widget.management.ManagementSprites;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.updateClient;
import static net.dillon.qualityofqueso.widget.management.ManagementSprites.managementIdentifier;

/**
 * Always quickly moves items when toggled.
 */
public class AlwaysQuickMoveButton extends ToggleableButton {

    public AlwaysQuickMoveButton(ManagementParams params) {
        super(params, b -> updateClient(ModClientOptions::toggleAlwaysQuickMove));
    }

    @Override
    protected boolean toggle() {
        return client().getAlwaysQuickMove();
    }

    @Override
    protected ManagementSprites getManagementSprites() {
        return new ManagementSprites(
                managementIdentifier("quick_move/always_quick_move"),
                managementIdentifier("quick_move/shift_to_quick_move")
        );
    }

    @Override
    protected Component getDefaultTooltip() {
        return Component.translatable(
                this.toggle()
                        ? "qualityofqueso.gui.always_quick_move"
                        : "qualityofqueso.gui.shift_to_quick_move"
        );
    }
}