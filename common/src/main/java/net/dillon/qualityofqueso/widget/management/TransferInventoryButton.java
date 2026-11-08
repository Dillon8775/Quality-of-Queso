package net.dillon.qualityofqueso.widget.management;

import static net.dillon.dillonlib.task.ClientTasks.getLocalPlayer;
import static net.dillon.qualityofqueso.helper.ModConstants.TRANSFER_INVENTORY_BUTTON_NAME;

/**
 * A management button that transfers items from inventory -> container.
 */
public class TransferInventoryButton extends ManagementButton {

    public TransferInventoryButton(ManagementParams params) {
        super(
                params,
                TRANSFER_INVENTORY_BUTTON_NAME,
                "transfer/inventory",
                NO_APPENDED_TOOLTIP,
                true,
                b -> params.managementEvents().transferEvents().transferItems(false, true)
        );
    }

    @Override
    protected boolean canBeActive() {
        return !managementEvents.isContainerFull(false)
                && managementEvents.shouldButtonBeActive(true, getLocalPlayer().getInventory(), true, true);
    }
}