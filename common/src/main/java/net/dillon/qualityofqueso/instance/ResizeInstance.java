package net.dillon.qualityofqueso.instance;

import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.dillon.qualityofqueso.instance.management.ManagementInstance;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;

import java.util.HashSet;
import java.util.Set;

import static net.dillon.qualityofqueso.helper.MethodHelper.getRecipeBookComponent;

/**
 * Handles screen resizing.
 */
public class ResizeInstance extends ManagementInstance {

    public ResizeInstance(QuesoScreen screen) {
        super(screen);
    }

    public void handleResizing(int width, int height, Set<Integer> excludedSlots) {
        // Get current text and focused status
        String text = instance().getSearchFields().searchText();
        boolean refocusContainerField = instance().getSearchFields().container() != null && instance().getSearchFields().container().isFocused();
        boolean refocusInventoryField = instance().getSearchFields().inventory() != null && instance().getSearchFields().inventory().isFocused();
        // Gets current tracked container
        boolean trackedContainer = ContainerHelper.IS_TRACKED_CONTAINER;
        // Prevents ConcurrentModificationException
        Set<Integer> temp = new HashSet<>(excludedSlots);
        excludedSlots.clear();
        // Refresh screen (or resize)
        instance().getScreen().init(width, height);
        if (!(instance().getScreen() instanceof AbstractRecipeBookScreen<?> recipeScreen && getRecipeBookComponent(recipeScreen).isVisible())) {
            excludedSlots.addAll(temp);
        }
        // Reset text and focused status on the re-initialized widgets
        if (instance().getSearchFields().container() != null) {
            instance().getSearchFields().container().setValue(text);
            instance().getSearchFields().container().setFocused(refocusContainerField);
            if (refocusContainerField) {
                instance().getScreen().setFocused(instance().getSearchFields().container());
            }
        }
        if (instance().getSearchFields().inventory() != null) {
            instance().getSearchFields().inventory().setValue(text);
            instance().getSearchFields().inventory().setFocused(refocusInventoryField);
            if (refocusInventoryField) {
                instance().getScreen().setFocused(instance().getSearchFields().inventory());
            }
        }
        // Reset tracked container
        ContainerHelper.IS_TRACKED_CONTAINER = trackedContainer;
    }
}
