package net.dillon.qualityofqueso.instance;

import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.dillon.qualityofqueso.instance.management.ManagementInstance;
import net.dillon.qualityofqueso.instance.management.TransferInstance;
import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.option.eum.management.sorting.CurrentSortingMode;
import net.dillon.qualityofqueso.option.eum.management.sorting.GlobalSortingMode;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.canScrollMoveAndHasScrollModifierDown;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.hasDropOnlyOneItemKeyDown;
import static net.dillon.qualityofqueso.util.ModConstants.*;

/**
 * Handles mouse scrolling events.
 */
public class MouseScrollInstance extends ManagementInstance {

    public MouseScrollInstance(QuesoScreen screen) {
        super(screen);
    }

    /**
     * Changes the sort mode when scrolling on the sort button.
     */
    public void changeSortMode(double scrollY) {
        if (buttonHoveredAndActive(instance().getManagementButtons().sort())) {
            CurrentSortingMode nextMode = clientOptionsInstance().getSortingOptions().currentSortingMode.next(scrollY > 0);
            ModClientOptions.INSTANCE.update(options -> {
                options.getSortingOptions().currentSortingMode = nextMode;
                if (clientOptionsInstance().getSortingOptions().currentSortingMode == CurrentSortingMode.ALPHABETICAL) {
                    options.getSortingOptions().globalSortingMode = GlobalSortingMode.ALPHABETICALLY;
                } else if (options.getSortingOptions().currentSortingMode == CurrentSortingMode.TAG) {
                    options.getSortingOptions().globalSortingMode = GlobalSortingMode.BY_TAG;
                } else if (options.getSortingOptions().currentSortingMode == CurrentSortingMode.COUNT_DESCENDING) {
                    options.getSortingOptions().globalSortingMode = GlobalSortingMode.DESCENDING;
                } else if (options.getSortingOptions().currentSortingMode == CurrentSortingMode.COUNT_ASCENDING) {
                    options.getSortingOptions().globalSortingMode = GlobalSortingMode.ASCENDING;
                } else if (options.getSortingOptions().currentSortingMode == CurrentSortingMode.CREATIVE_MENU) {
                    options.getSortingOptions().globalSortingMode = GlobalSortingMode.CREATIVE_MENU;
                }
            });
            ContainerHelper.storeActiveSortMode(nextMode);
            if (SORT_SOUND_COOLDOWN == 0) {
                SORT_SOUND_COOLDOWN = DEFAULT_SORT_SOUND_COOLDOWN;
                playSortSound(instance().getMinecraft());
            }
        }
    }

    /**
     * Changes filter type (item/tag) when scrolling on the filtering button.
     */
    public void changeFilterType(double scrollY) {
        if (scrollY == 0) {
            return;
        }
        if (!ContainerHelper.isTrackedFilteringActive()) {
            return;
        }
        if (!buttonHoveredAndActive(instance().getManagementButtons().filtering())) {
            return;
        }

        ContainerHelper.toggleCurrentFilterMode();
        playSortSound(instance().getMinecraft());
    }

    /**
     * Sets the move amount when scrolling.
     */
    public void setMoveAmount(Slot hoveredSlot, double scrollY) {
        if (!clientOptionsInstance().getManagementOptions().scrollMoving) {
            return;
        }

        try {
            if (canScrollMoveAndHasScrollModifierDown() && (instance().getManagementButtons().transferContainer().isHovered() || instance().getManagementButtons().transferInventory().isHovered())) {
                changeMountAmount(hoveredSlot, scrollY);
            }
        } catch (NullPointerException o) {
        }

        if (lockedSlotsInstance().shouldCancelDrop()) {
            return;
        }

        boolean validHoveredSlot = hoveredSlotHasItem(hoveredSlot) && hoveredSlot.getItem().getCount() > 1;
        if (!isCreativeInventoryScreen(instance().getScreen()) && instance().getCanMoveOne() && (validHoveredSlot && hasDropOnlyOneItemKeyDown())
                || buttonHoveredAndActive(instance().getManagementButtons().quickDrop()) ? hasDropOnlyOneItemKeyDown()
                : ((validHoveredSlot || buttonHoveredAndActive(instance().getManagementButtons().transferContainer()) || buttonHoveredAndActive(instance().getManagementButtons().transferInventory())) && canScrollMoveAndHasScrollModifierDown())) {
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
     *
     * @return {@code true} if the scroll was consumed.
     */
    public void moveHoveredItem(double scrollY, CallbackInfoReturnable<Boolean> cir) {
        if (!isCreativeInventoryScreen(instance().getScreen())
                && clientOptionsInstance().getManagementOptions().scrollMoving
                && canScrollMoveAndHasScrollModifierDown()
                && !hasDropOnlyOneItemKeyDown()
                && new TransferInstance(instance()).tryMoveSingleFromScroll(instance().getScreensHoveredSlot(), scrollY)) {
            cir.setReturnValue(true);
        } else {
            setMoveAmount(instance().getScreensHoveredSlot(), scrollY);
        }
    }
}