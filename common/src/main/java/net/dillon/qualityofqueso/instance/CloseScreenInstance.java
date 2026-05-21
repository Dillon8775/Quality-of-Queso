package net.dillon.qualityofqueso.instance;

import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.dillon.qualityofqueso.helper.MethodHelper;
import net.dillon.qualityofqueso.instance.management.ManagementInstance;
import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.option.eum.management.FilteringMode;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

import java.util.HashSet;

import static net.dillon.qualityofqueso.helper.ManagementHelper.isContainerScreen;
import static net.dillon.qualityofqueso.helper.ManagementHelper.isInventoryScreen;
import static net.dillon.qualityofqueso.helper.MethodHelper.getRecipeBookComponent;
import static net.dillon.qualityofqueso.helper.ModHelper.options;
import static net.dillon.qualityofqueso.util.ModConstants.*;

/**
 * Handles closing of screens.
 */
public class CloseScreenInstance extends ManagementInstance {

    public CloseScreenInstance(QuesoScreen screen) {
        super(screen);
    }

    /**
     * Adds excluded slots to store when closing the screen.
     */
    public void putExcludedSlots() {
        if (options().management.saveExcludedSlots) {
            SAVED_EXCLUDED_SLOTS.put(getTotalSlots(), new HashSet<>(instance().getExcludedSlots()));
        }
    }

    /**
     * Saves the search text for search fields.
     */
    public void saveSearchText() {
        if (options().searching.saveSearchText) {
            if (isInventoryScreen(instance().getScreen()) && instance().getSearchFields().inventory() != null) {
                SAVED_TEXT = instance().getSearchFields().inventory().getValue();
            } else if (isContainerScreen(instance().getScreen()) && instance().getSearchFields().container() != null) {
                SAVED_TEXT = instance().getSearchFields().container().getValue();
            }
        }
    }

    /**
     * Disables certain features, like craft all and trade all.
     */
    public void disableFeatures() {
        if (!options().buttonDisplayOptions.safeBulk) {
            return;
        }

        ModClientOptions.INSTANCE.update(options -> {
            options.management.bulkCraft = false;
            options.management.bulkTrade = false;
        });
    }

    /**
     * Automatically closes the recipe book when closing a screen.
     */
    public void autoCloseRecipeBook() {
        if (options().misc.autoCloseRecipeBook
                && instance().getScreen() instanceof InventoryScreen recipeBookScreen
                && getRecipeBookComponent(recipeBookScreen).isVisible()) {
            getRecipeBookComponent(recipeBookScreen).toggleVisibility();
            MethodHelper.refreshWidgets(instance().getScreen());
        }
    }

    /**
     * Handles tracked containers when closing a screen.
     */
    public void handleTrackedContainers() {
        if (instance().getDisableFilteringOnClose()) {
            ModClientOptions.INSTANCE.update(options -> {
                options.management.filteringMode = FilteringMode.NONE;
            });
        }

        if (isContainerScreen(instance().getScreen())) {
            if (ContainerHelper.OPENING_PLACEHOLDER_SCREEN) {
                ContainerHelper.OPENING_PLACEHOLDER_SCREEN = false;
                return;
            }
            ContainerHelper.clearActiveContainer();
            ContainerHelper.IS_TRACKED_CONTAINER = false;
            if (!options().sorting.useGlobalSortingMode) {
                options().sorting.currentSortingMode = GLOBAL_SORTING_MODE;
            }
        }
    }
}
