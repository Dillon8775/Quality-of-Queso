package net.dillon.qualityofqueso.event;

import net.minecraft.client.gui.components.EditBox;

/**
 * Exposes initialization methods, and buttons and search fields.
 */
public interface WidgetHandler {

    /**
     * Sets the container search field.
     */
    void setContainerSearchField(EditBox containerSearchField);

    /**
     * Sets the inventory search field.
     */
    void setInventorySearchField(EditBox inventorySearchField);
}