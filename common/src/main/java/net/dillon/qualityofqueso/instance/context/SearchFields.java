package net.dillon.qualityofqueso.instance.context;

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