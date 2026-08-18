package net.dillon.qualityofqueso.instance;

import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.dillon.qualityofqueso.helper.MethodHelper;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.instance.management.ManagementInstance;
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
public class ScreenInitInstance extends ManagementInstance {

    public ScreenInitInstance(QuesoScreen screen) {
        super(screen);
    }

    /**
     * Handles tracked containers upon screen creation.
     */
    public void handleTrackedContainers() {
        if (isContainerScreen(instance().getScreen())) {
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
                            instance().setDisableFilteringOnClose(true);
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
        if (isContainerScreen(instance().getScreen()) && client().searching().containerSearching) {
            // Initialize the container search field, if it should be initialized
            widgetHandler().setContainerSearchField(searchInstance().initializeSearchField(false));
            MethodHelper.addRenderableModWidget(instance().getScreen(), instance().getSearchFields().container());
        } else if (isInventoryScreen(instance().getScreen())) { // Initialize the inventory search field, if it should be initialized
            // Also initialize the "container" variable to the player's inventory, if the container was never initialized from any of the other screens
            instance().setCachedContainer(instance().getMinecraft().player.getInventory());
            if (client().searching().inventorySearching) {
                widgetHandler().setInventorySearchField(searchInstance().initializeSearchField(true));
                MethodHelper.addRenderableModWidget(instance().getScreen(), instance().getSearchFields().inventory());
            }
        }
    }

    /**
     * Re-adds all excluded slots to the screen.
     */
    public void readdExcludedSlots() {
        if (isValidScreen(instance().getScreen()) && ModConstants.SAVING_EXCLUDED_SLOTS && instance().getCachedContainer() != null) {
            // Do not re-add excluded slots if the recipe book is open, because it breaks things
            if (instance().getScreen() instanceof AbstractRecipeBookScreen<?> recipeScreen && getRecipeBookComponent(recipeScreen).isVisible()) {
                return;
            }

            // Otherwise, re-add all excluded slots to the screen (if the screen size is equal to a last saved excluded slot size)
            instance().getExcludedSlots().clear();
            if (SAVED_EXCLUDED_SLOTS.containsKey(getTotalSlots())) {
                instance().getExcludedSlots().addAll(SAVED_EXCLUDED_SLOTS.get(getTotalSlots()));
            }
        }
    }

    /**
     * Initializes the container in a screen.
     */
    public void initializeContainer() {
        // Figure out what the appropriate "container" should be for this instance of a screen
        // Check brewing stands, furnaces, dispeners/droppers, and hoppers first
        if (instance().getScreen() instanceof BrewingStandScreen brewingStandScreen) {
            instance().setCachedContainer(brewingStand(brewingStandScreen));
        } else if (instance().getScreen() instanceof AbstractFurnaceScreen<?> abstractFurnaceScreen) {
            instance().setCachedContainer(abstractFurnaceScreen.getMenu().getResultSlot().container);
        } else if (instance().getScreen() instanceof DispenserScreen dispenserScreen) {
            instance().setCachedContainer(dispenser(dispenserScreen));
        } else if (instance().getScreen() instanceof HopperScreen hopperScreen) {
            instance().setCachedContainer(hopper(hopperScreen));
        }
        // If screen isn't an instance of any of the above, then start checking for container/inventory screens
        if (isContainerScreen(instance().getScreen())) {
            // Determine fromInventory variable; if instance ShulkerBoxScreen, fromInventory is the shulker box's fromInventory
            if (instance().getScreen() instanceof ShulkerBoxScreen shulkerBoxScreen) {
                instance().setCachedContainer(shulkerBox(shulkerBoxScreen));
            }
            // If it's GenericContainerScreen, it's the generic container (or most likely chest/barrel)'s fromInventory
            else if (instance().getScreen() instanceof ContainerScreen genericContainerScreen) {
                instance().setCachedContainer(genericContainerScreen.getMenu().getContainer());
            }
            // Otherwise, fromInventory is null
            else {
                instance().setCachedContainer(null);
            }
        }
    }

    /**
     * Resets the "current container" variable.
     */
    public void setCurrentContainer() {
        CURRENT_CONTAINER = instance().getCachedContainer();
    }
}