package net.dillon.qualityofqueso.instance;

import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.dillon.qualityofqueso.instance.context.SearchFields;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;

import java.util.HashSet;
import java.util.Set;

import static net.dillon.qualityofqueso.helper.MethodHelper.getRecipeBookComponent;

/**
 * Handles screen resizing.
 */
public record ResizeInstance(
        AbstractContainerScreen<?> screen,
        SearchFields searchFields
) implements ModInstance {

    public void handleResizing(int width, int height, Set<Integer> excludedSlots) {
        // Get current text and focused status
        String text = searchFields.searchText();
        boolean refocusContainerField = instance().getSearchFields().container() != null && instance().getSearchFields().container().isFocused();
        boolean refocusInventoryField = instance().getSearchFields().inventory() != null && instance().getSearchFields().inventory().isFocused();
        // Gets current tracked container
        boolean trackedContainer = ContainerHelper.IS_TRACKED_CONTAINER;
        // Prevents ConcurrentModificationException
        Set<Integer> temp = new HashSet<>(excludedSlots);
        excludedSlots.clear();
        // Refresh screen (or resize)
        screen.init(width, height);
        if (!(screen instanceof AbstractRecipeBookScreen<?> recipeScreen && getRecipeBookComponent(recipeScreen).isVisible())) {
            excludedSlots.addAll(temp);
        }
        // Reset text and focused status on the re-initialized widgets
        if (instance().getSearchFields().container() != null) {
            instance().getSearchFields().container().setValue(text);
            instance().getSearchFields().container().setFocused(refocusContainerField);
            if (refocusContainerField) {
                screen.setFocused(instance().getSearchFields().container());
            }
        }
        if (instance().getSearchFields().inventory() != null) {
            instance().getSearchFields().inventory().setValue(text);
            instance().getSearchFields().inventory().setFocused(refocusInventoryField);
            if (refocusInventoryField) {
                screen.setFocused(instance().getSearchFields().inventory());
            }
        }
        // Reset tracked container
        ContainerHelper.IS_TRACKED_CONTAINER = trackedContainer;
    }

    @Override
    public QuesoScreen instance() {
        return (QuesoScreen) this.screen;
    }
}
