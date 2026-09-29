package net.dillon.qualityofqueso.event.context;

import net.minecraft.client.gui.components.EditBox;

/**
 * Holds search fields.
 */
public record SearchFields(
        EditBox container,
        EditBox inventory,
        String searchText
) {
}