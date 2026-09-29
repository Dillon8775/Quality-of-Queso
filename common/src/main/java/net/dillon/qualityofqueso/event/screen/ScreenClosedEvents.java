package net.dillon.qualityofqueso.event.screen;

import net.dillon.qualityofqueso.event.QuesoScreen;
import net.dillon.qualityofqueso.event.management.CursorKey;
import net.dillon.qualityofqueso.event.management.ManagementEvents;
import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.dillon.qualityofqueso.helper.MethodHelper;
import net.dillon.qualityofqueso.option.eum.management.FilteringMode;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;

import java.util.HashSet;

import static net.dillon.qualityofqueso.event.management.ExtractingEvents.setCursor;
import static net.dillon.qualityofqueso.helper.ManagementHelper.isContainerScreen;
import static net.dillon.qualityofqueso.helper.ManagementHelper.isInventoryScreen;
import static net.dillon.qualityofqueso.helper.MethodHelper.getRecipeBookComponent;
import static net.dillon.qualityofqueso.helper.ModConstants.*;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.updateClient;

/**
 * Handles closing of screens.
 */
public class ScreenClosedEvents extends ManagementEvents {

    public ScreenClosedEvents(QuesoScreen screen) {
        super(screen);
    }

    /**
     * Adds excluded slots to store when closing the screen.
     */
    public void putExcludedSlots() {
        if (SAVING_EXCLUDED_SLOTS) {
            SAVED_EXCLUDED_SLOTS.put(getTotalSlots(), new HashSet<>(holder().excludedSlots()));
        }
    }

    /**
     * Saves the search text for search fields.
     */
    public void saveSearchText() {
        if (client().searching().saveSearchText) {
            if (isInventoryScreen(holder().screen()) && holder().searchFields().inventory() != null) {
                updateClient(client -> client.searching().savedSearchText = holder().searchFields().inventory().getValue());
            } else if (isContainerScreen(holder().screen()) && holder().searchFields().container() != null) {
                updateClient(client -> client.searching().savedSearchText = holder().searchFields().container().getValue());
            }
        }
    }

    /**
     * Disables certain features, like craft all and trade all.
     */
    public void disableFeatures() {
        setCursor(CursorKey.NULL);

        if (!client().buttonDisplayOptions().safeBulk) {
            return;
        }

        updateClient(client -> {
            client.management().bulkCraft = false;
            client.management().bulkTrade = false;
        });
    }

    /**
     * Automatically closes the recipe book when closing a screen.
     */
    public void autoCloseRecipeBook() {
        if (client().misc().autoCloseRecipeBook
                && holder().screen() instanceof AbstractRecipeBookScreen<?> recipeBookScreen
                && getRecipeBookComponent(recipeBookScreen).isVisible()) {
            getRecipeBookComponent(recipeBookScreen).toggleVisibility();
            MethodHelper.refreshWidgets(holder().screen());
        }
    }

    /**
     * Handles tracked containers when closing a screen.
     */
    public void handleTrackedContainers() {
        if (holder().getDisableFilteringOnClose()) {
            updateClient(client -> {
                client.management().filteringMode = FilteringMode.NONE;
            });
        }

        if (isContainerScreen(holder().screen())) {
            if (ContainerHelper.OPENING_PLACEHOLDER_SCREEN) {
                ContainerHelper.OPENING_PLACEHOLDER_SCREEN = false;
                return;
            }
            ContainerHelper.clearActiveContainer();
            ContainerHelper.IS_TRACKED_CONTAINER = false;
            if (!client().sorting().useGlobalSortingMode) {
                client().sorting().currentSortingMode = GLOBAL_SORTING_MODE;
            }
        }
    }
}