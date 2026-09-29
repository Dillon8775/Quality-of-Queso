package net.dillon.qualityofqueso.event.screen;

import net.dillon.qualityofqueso.event.QuesoScreenHolder;
import net.dillon.qualityofqueso.event.management.ManagementEvents;
import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;

import java.util.HashSet;
import java.util.Set;

import static net.dillon.qualityofqueso.helper.MethodHelper.getRecipeBookComponent;

/**
 * Handles screen resizing.
 */
public class ScreenResizedEvents extends ManagementEvents {

    public ScreenResizedEvents(QuesoScreenHolder screen) {
        super(screen);
    }

    public void handleResizing(int width, int height, Set<Integer> excludedSlots) {
        // Get current text and focused status
        String text = holder().searchFields().searchText();
        boolean refocusContainerField = holder().searchFields().container() != null && holder().searchFields().container().isFocused();
        boolean refocusInventoryField = holder().searchFields().inventory() != null && holder().searchFields().inventory().isFocused();
        // Gets current tracked container
        boolean trackedContainer = ContainerHelper.IS_TRACKED_CONTAINER;
        // Prevents ConcurrentModificationException
        Set<Integer> temp = new HashSet<>(excludedSlots);
        excludedSlots.clear();
        // Refresh screen (or resize)
        holder().screen().init(width, height);
        if (!(holder().screen() instanceof AbstractRecipeBookScreen<?> recipeScreen && getRecipeBookComponent(recipeScreen).isVisible())) {
            excludedSlots.addAll(temp);
        }
        // Reset text and focused status on the re-initialized widgets
        if (holder().searchFields().container() != null) {
            holder().searchFields().container().setValue(text);
            holder().searchFields().container().setFocused(refocusContainerField);
            if (refocusContainerField) {
                holder().screen().setFocused(holder().searchFields().container());
            }
        }
        if (holder().searchFields().inventory() != null) {
            holder().searchFields().inventory().setValue(text);
            holder().searchFields().inventory().setFocused(refocusInventoryField);
            if (refocusInventoryField) {
                holder().screen().setFocused(holder().searchFields().inventory());
            }
        }
        // Reset tracked container
        ContainerHelper.IS_TRACKED_CONTAINER = trackedContainer;
    }
}
