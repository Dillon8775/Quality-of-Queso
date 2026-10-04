package net.dillon.qualityofqueso.event.mouse;

import net.dillon.qualityofqueso.event.QuesoScreenHolder;
import net.dillon.qualityofqueso.event.management.CursorKey;
import net.dillon.qualityofqueso.event.management.ManagementEvents;
import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.dillon.qualityofqueso.option.eum.management.sorting.CurrentSortingMode;
import net.dillon.qualityofqueso.option.eum.management.sorting.GlobalSortingMode;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.event.management.ExtractingEvents.setCursor;
import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.ModConstants.MOVE_AMOUNT;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.canScrollMoveAndHasScrollModifierDown;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.hasDropOnlyOneItemModifierDown;
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
    public void handle(double scrollY, CallbackInfoReturnable<Boolean> cir) {
        changeSortMode(scrollY);
        changeFilterType(scrollY);
        moveHoveredItem(scrollY, cir);
    }

    /**
     * Changes the sort mode when scrolling on the sort button.
     */
    private void changeSortMode(double scrollY) {
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
            ContainerHelper.storeActiveSortMode(nextMode);
            playSafeSortSound();
        }
    }

    /**
     * Changes filter type (item/tag) when scrolling on the filtering button.
     */
    private void changeFilterType(double scrollY) {
        if (scrollY == 0) {
            return;
        }
        if (!ContainerHelper.isTrackedFilteringActive()) {
            return;
        }
        if (!buttonHoveredAndActive(holder().managementButtons().getFiltering())) {
            return;
        }

        ContainerHelper.toggleCurrentFilterMode();
        playSortSound();
    }

    /**
     * Sets the move amount when scrolling.
     */
    private void setMoveAmount(Slot hoveredSlot, double scrollY) {
        if (!client().management().scrollMoving) {
            return;
        }

        try {
            if (canScrollMoveAndHasScrollModifierDown() && (holder().managementButtons().getTransferContainer().isHovered() || holder().managementButtons().getTransferInventory().isHovered())) {
                changeMountAmount(hoveredSlot, scrollY);
            }
        } catch (NullPointerException o) {
        }

        if (lockedSlotEvents().shouldCancelDrop()) {
            return;
        }

        boolean validHoveredSlot = hoveredSlotHasItem(hoveredSlot) && hoveredSlot.getItem().count() > 1;
        if (!isCreativeInventoryScreen(holder().screen()) && holder().getCanMoveOne() && (validHoveredSlot && hasDropOnlyOneItemModifierDown())
                || buttonHoveredAndActive(holder().managementButtons().getQuickDrop()) ? hasDropOnlyOneItemModifierDown()
                : ((validHoveredSlot || buttonHoveredAndActive(holder().managementButtons().getTransferContainer()) || buttonHoveredAndActive(holder().managementButtons().getTransferInventory())) && canScrollMoveAndHasScrollModifierDown())) {
            changeMountAmount(hoveredSlot, scrollY);
        }
    }

    /**
     * Changes the move amount.
     */
    private void changeMountAmount(Slot hoveredSlot, double scrollY) {
        MOVE_AMOUNT += (int)scrollY;
        if (MOVE_AMOUNT < 1) {
            MOVE_AMOUNT = 1;
        } else if (MOVE_AMOUNT > 64) {
            MOVE_AMOUNT = 64;
        } else if (hoveredSlotHasItem(hoveredSlot) && MOVE_AMOUNT > hoveredSlot.getItem().getMaxStackSize()) {
            MOVE_AMOUNT = hoveredSlot.getItem().getMaxStackSize();
        }
    }

    /**
     * Moves one hovered item by scrolling.
     */
    private void moveHoveredItem(double scrollY, CallbackInfoReturnable<Boolean> cir) {
        if (!isCreativeInventoryScreen(holder().screen())
                && client().management().scrollMoving
                && canScrollMoveAndHasScrollModifierDown()
                && !hasDropOnlyOneItemModifierDown()
                && transferEvents().tryMoveSingleFromScroll(holder().screensHoveredSlot(), scrollY)) {
            setCursor(CursorKey.MOVE);
            playSafeSortSound();
            cir.setReturnValue(true);
        } else {
            setMoveAmount(holder().screensHoveredSlot(), scrollY);
        }
    }
}