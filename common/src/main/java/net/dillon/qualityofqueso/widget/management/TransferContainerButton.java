package net.dillon.qualityofqueso.widget.management;

import static net.dillon.qualityofqueso.helper.ModConstants.TRANSFER_CONTAINER_BUTTON_NAME;

/**
 * A management button that transfers items from container -> inventory.
 */
public class TransferContainerButton extends ManagementButton {

    public TransferContainerButton(ManagementParams params) {
        super(
                params,
                TRANSFER_CONTAINER_BUTTON_NAME,
                "transfer/container",
                NO_APPENDED_TOOLTIP,
                true,
                b -> params.managementEvents().transferEvents().transferItems(true, true)
        );
    }

    @Override
    protected boolean canBeActive() {
        return !managementEvents.isContainerFull(true)
                && managementEvents.shouldButtonBeActive(false, null, true, true);
    }
}