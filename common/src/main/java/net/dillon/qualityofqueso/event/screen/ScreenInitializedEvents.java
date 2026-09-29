package net.dillon.qualityofqueso.event.screen;

import net.dillon.qualityofqueso.event.QuesoScreenHolder;
import net.dillon.qualityofqueso.event.management.ManagementEvents;
import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.dillon.qualityofqueso.helper.MethodHelper;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.option.eum.management.FilteringMode;
import net.minecraft.client.gui.screens.inventory.*;

import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.MethodHelper.*;
import static net.dillon.qualityofqueso.helper.ModConstants.CURRENT_CONTAINER;
import static net.dillon.qualityofqueso.helper.ModConstants.SAVED_EXCLUDED_SLOTS;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.updateClient;

/**
 * Handles screen creation, with creating and initializing the correct variables.
 */
public class ScreenInitializedEvents extends ManagementEvents {

    public ScreenInitializedEvents(QuesoScreenHolder screen) {
        super(screen);
    }

    /**
     * Handles tracked containers upon screen creation.
     */
    public void handleTrackedContainers() {
        if (isContainerScreen(holder().screen())) {
            // Handle tracked containers
            if (ContainerHelper.RETURNING_FROM_PLACEHOLDER_SCREEN) {
                // If the user is returning from a placeholder screen, then we should re-track the container as "filtered", to ensure no variables are lost
                ContainerHelper.RETURNING_FROM_PLACEHOLDER_SCREEN = false;
                ContainerHelper.IS_TRACKED_CONTAINER = true;
            } else if (ContainerHelper.consumePendingOpenIsTracked()) { // Next, check if the container is tracked. Then temporarily set "fill what's present" to true, so that filtering works correctly. Once the screen closes, disable "fill what's present"
                ContainerHelper.IS_TRACKED_CONTAINER = true;
                if (client().management().containerFiltering) {
                    updateClient(client -> {
                        if (!client.isFiltering()) {
                            client.management().filteringMode = FilteringMode.MATCHING;
                            holder().setDisableFilteringOnClose(true);
                        }
                    });
                }
            } else { // Otherwise, the container isn't tracked, so mark it as "not tracked"
                ContainerHelper.IS_TRACKED_CONTAINER = false;
            }
            return;
        }

        // Prevent stale tracked state from leaking into non-container screens (e.g., inventory).
        ContainerHelper.IS_TRACKED_CONTAINER = false;
    }

    /**
     * Initializes search fields for the screen.
     */
    public void initializeSearchFields() {
        if (isContainerScreen(holder().screen()) && client().searching().containerSearching) {
            // Initialize the container search field, if it should be initialized
            holder().setContainerSearchField(searchEvents().initializeSearchField(false));
            MethodHelper.addRenderableModWidget(holder().screen(), holder().searchFields().container());
        } else if (isInventoryScreen(holder().screen())) { // Initialize the inventory search field, if it should be initialized
            // Also initialize the "container" variable to the player's inventory, if the container was never initialized from any of the other screens
            holder().setCachedContainer(holder().mc().player.getInventory());
            if (client().searching().inventorySearching) {
                holder().setInventorySearchField(searchEvents().initializeSearchField(true));
                MethodHelper.addRenderableModWidget(holder().screen(), holder().searchFields().inventory());
            }
        }
    }

    /**
     * Re-adds all excluded slots to the screen.
     */
    public void readdExcludedSlots() {
        if (isValidScreen(holder().screen()) && ModConstants.SAVING_EXCLUDED_SLOTS && holder().getCachedContainer() != null) {
            // Do not re-add excluded slots if the recipe book is open, because it breaks things
            if (holder().screen() instanceof AbstractRecipeBookScreen<?> recipeScreen && getRecipeBookComponent(recipeScreen).isVisible()) {
                return;
            }

            // Otherwise, re-add all excluded slots to the screen (if the screen size is equal to a last saved excluded slot size)
            holder().excludedSlots().clear();
            if (SAVED_EXCLUDED_SLOTS.containsKey(getTotalSlots())) {
                holder().excludedSlots().addAll(SAVED_EXCLUDED_SLOTS.get(getTotalSlots()));
            }
        }
    }

    /**
     * Initializes the container in a screen.
     */
    public void initializeContainer() {
        // Figure out what the appropriate "container" should be for this instance of a screen
        // Check brewing stands, furnaces, dispeners/droppers, and hoppers first
        if (holder().screen() instanceof BrewingStandScreen brewingStandScreen) {
            holder().setCachedContainer(brewingStand(brewingStandScreen));
        } else if (holder().screen() instanceof AbstractFurnaceScreen<?> abstractFurnaceScreen) {
            holder().setCachedContainer(abstractFurnaceScreen.getMenu().getResultSlot().container);
        } else if (holder().screen() instanceof DispenserScreen dispenserScreen) {
            holder().setCachedContainer(dispenser(dispenserScreen));
        } else if (holder().screen() instanceof HopperScreen hopperScreen) {
            holder().setCachedContainer(hopper(hopperScreen));
        }
        // If screen isn't an instance of any of the above, then start checking for container/inventory screens
        if (isContainerScreen(holder().screen())) {
            // Determine fromInventory variable; if instance ShulkerBoxScreen, fromInventory is the shulker box's fromInventory
            if (holder().screen() instanceof ShulkerBoxScreen shulkerBoxScreen) {
                holder().setCachedContainer(shulkerBox(shulkerBoxScreen));
            }
            // If it's GenericContainerScreen, it's the generic container (or most likely chest/barrel)'s fromInventory
            else if (holder().screen() instanceof ContainerScreen genericContainerScreen) {
                holder().setCachedContainer(genericContainerScreen.getMenu().getContainer());
            }
            // Otherwise, fromInventory is null
            else {
                holder().setCachedContainer(null);
            }
        }
    }

    /**
     * Resets the "current container" variable.
     */
    public void setCurrentContainer() {
        CURRENT_CONTAINER = holder().getCachedContainer();
    }
}