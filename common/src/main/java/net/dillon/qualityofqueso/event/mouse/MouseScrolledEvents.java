package net.dillon.qualityofqueso.event.mouse;

import net.dillon.qualityofqueso.container.ContainerSyncer;
import net.dillon.qualityofqueso.event.QuesoScreenHolder;
import net.dillon.qualityofqueso.event.management.CursorKey;
import net.dillon.qualityofqueso.event.management.ManagementEvents;
import net.dillon.qualityofqueso.option.eum.management.sorting.CurrentSortingMode;
import net.dillon.qualityofqueso.option.eum.management.sorting.GlobalSortingMode;
import net.dillon.qualityofqueso.sound.BundleRemoveOneSoundEvents;
import net.minecraft.client.gui.components.inventory.SlotWidget;

import static net.dillon.qualityofqueso.event.management.ExtractingEvents.setCursor;
import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.ModConstants.MOVE_AMOUNT;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.canScrollMoveAndHasScrollModifierDown;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.hasDropOnlyOneItemModifierDown;
import static net.dillon.qualityofqueso.helper.SoundHelper.playSafeSortSound;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.updateClient;

/**
 * Handles mouse scrolling events.
 */
public class MouseScrolledEvents extends ManagementEvents {

    public MouseScrolledEvents(QuesoScreenHolder screen) {
        super(screen);
    }

    /**
     * All mouse scrolled events.
     */
    public boolean handle(double scrollY) {
        if (changeSortMode(scrollY)) {
            return true;
        }

        if (changeFilterType(scrollY)) {
            return true;
        }

        return moveSingularHoveredItem(scrollY);
    }

    /**
     * Changes the sort mode when scrolling on the sort button.
     */
    private boolean changeSortMode(double scrollY) {
        if (buttonHoveredAndActive(holder().managementButtons().getSort())) {
            CurrentSortingMode nextMode = client().sorting().currentSortingMode.next(scrollY > 0);
            updateClient(client -> {
                client.sorting().currentSortingMode = nextMode;
                if (client().sorting().currentSortingMode == CurrentSortingMode.ALPHABETICAL) {
                    client.sorting().globalSortingMode = GlobalSortingMode.ALPHABETICALLY;
                } else if (client.sorting().currentSortingMode == CurrentSortingMode.TAG) {
                    client.sorting().globalSortingMode = GlobalSortingMode.BY_TAG;
                } else if (client.sorting().currentSortingMode == CurrentSortingMode.COUNT_DESCENDING) {
                    client.sorting().globalSortingMode = GlobalSortingMode.DESCENDING;
                } else if (client.sorting().currentSortingMode == CurrentSortingMode.COUNT_ASCENDING) {
                    client.sorting().globalSortingMode = GlobalSortingMode.ASCENDING;
                } else if (client.sorting().currentSortingMode == CurrentSortingMode.CREATIVE_MENU) {
                    client.sorting().globalSortingMode = GlobalSortingMode.CREATIVE_MENU;
                }
            });
            ContainerSyncer.storeActiveSortMode(nextMode);
            playSafeSortSound(BundleRemoveOneSoundEvents.SORT_MODE_CHANGED);
            return true;
        }

        return false;
    }

    /**
     * Changes filter type (item/tag) when scrolling on the filtering button.
     */
    private boolean changeFilterType(double scrollY) {
        if (scrollY == 0) {
            return false;
        }

        if (!ContainerSyncer.isTrackedFilteringActive()) {
            return false;
        }

        if (!buttonHoveredAndActive(holder().managementButtons().getFiltering())) {
            return false;
        }

        ContainerSyncer.toggleCurrentFilterMode();
        playSafeSortSound(BundleRemoveOneSoundEvents.FILTER_MODE_CHANGED);
        return true;
    }

    /**
     * Sets the move amount when scrolling.
     */
    private boolean setMoveAmount(SlotWidget hoveredSlotWidget, double scrollY) {
        if (!client().management().scrollMoving) {
            return false;
        }

        try {
            // Change move amount on transfer buttons
            if (canScrollMoveAndHasScrollModifierDown()
                    && (holder().managementButtons().getTransferContainer().isHovered() || holder().managementButtons().getTransferInventory().isHovered())) {
                return changeMountAmount(hoveredSlotWidget, scrollY);
            }
        } catch (NullPointerException o) {
            // Sometimes, a null pointer exception is thrown from this. Do nothing if caught
        }

        if (lockedSlotEvents().shouldCancelDrop(hoveredSlotWidget)) {
            // Return false if dropped canceled
            return false;
        }

        boolean validHoveredSlot = hoveredSlotHasItem(hoveredSlotWidget) && hoveredSlotWidget.slot().getItem().count() > 1;
        if (!isCreativeInventoryScreen()
                && holder().getCanMoveOne()
                && (validHoveredSlot && hasDropOnlyOneItemModifierDown())
                || buttonHoveredAndActive(holder().managementButtons().getQuickDrop()) ? hasDropOnlyOneItemModifierDown()
                : (
                (
                        validHoveredSlot || buttonHoveredAndActive(holder().managementButtons().getTransferContainer()) || buttonHoveredAndActive(holder().managementButtons().getTransferInventory())
                ) && canScrollMoveAndHasScrollModifierDown()
        )) {
            return changeMountAmount(hoveredSlotWidget, scrollY);
        }

        return false;
    }

    /**
     * Changes the move amount.
     */
    private boolean changeMountAmount(SlotWidget hoveredSlotWidget, double scrollY) {
        // Increment move amount
        MOVE_AMOUNT += (int) scrollY;

        // Cap move amount
        if (MOVE_AMOUNT < 1) {
            MOVE_AMOUNT = 1;
        } else if (MOVE_AMOUNT > 64) {
            MOVE_AMOUNT = 64;
        } else {
            // Ensure move amount cannot go over slot stack size if it's not null
            if (hoveredSlotHasItem(hoveredSlotWidget) && MOVE_AMOUNT > hoveredSlotWidget.slot().getItem().getMaxStackSize()) {
                MOVE_AMOUNT = hoveredSlotWidget.slot().getItem().getMaxStackSize();
            }
        }

        return true;
    }

    /**
     * Moves one hovered item by scrolling.
     */
    private boolean moveSingularHoveredItem(double scrollY) {
        if (!isCreativeInventoryScreen()
                && client().management().scrollMoving
                && canScrollMoveAndHasScrollModifierDown()
                && !hasDropOnlyOneItemModifierDown()
                && transferEvents().tryMoveSingleFromScroll(holder().getHoveredSlotWidget(), scrollY)) {
            // Single move item, return true that moved
            setCursor(CursorKey.MOVE);
            playSafeSortSound(BundleRemoveOneSoundEvents.SINGLE_MOVE);
            return true;
        } else {
            // Set move amount for other reasons (hovered buttons)
            return setMoveAmount(holder().getHoveredSlotWidget(), scrollY);
        }
    }
}