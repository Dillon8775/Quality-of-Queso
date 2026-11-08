package net.dillon.qualityofqueso.widget.management;

import net.dillon.qualityofqueso.event.management.ManagementEvents;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Stores common parameters for the {@link ManagementButton}.
 */
public record ManagementParams(
        AbstractContainerMenu menu,
        ManagementEvents managementEvents
) {
}