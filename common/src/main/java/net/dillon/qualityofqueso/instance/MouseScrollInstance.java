package net.dillon.qualityofqueso.instance;

import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.dillon.qualityofqueso.instance.context.ManagementButtons;
import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.option.eum.management.sorting.CurrentSortingMode;
import net.dillon.qualityofqueso.option.eum.management.sorting.GlobalSortingMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;

import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.ModHelper.options;
import static net.dillon.qualityofqueso.keybind.ModKeybinds.hasDropOnlyOneItemKeyDown;
import static net.dillon.qualityofqueso.keybind.ModKeybinds.hasMoveSingleModifierDown;
import static net.dillon.qualityofqueso.util.ModConstants.*;

/**
 * Handles mouse scrolling events.
 */
public record MouseScrollInstance(
        Minecraft minecraft,
        AbstractContainerScreen<?> screen,
        ManagementButtons managementButtons
) implements ModInstance {

    /**
     * Changes the sort mode when scrolling on the sort button.
     */
    public void changeSortMode(double mouseX, double mouseY, double scrollY) {
        if (buttonHoveredAndActive(managementButtons.sort(), mouseX, mouseY)) {
            CurrentSortingMode nextMode = options().sorting.currentSortingMode.next(scrollY > 0);
            ModClientOptions.INSTANCE.update(options -> {
                options.sorting.currentSortingMode = nextMode;
                if (options().sorting.currentSortingMode == CurrentSortingMode.ALPHABETICAL) {
                    options.sorting.globalSortingMode = GlobalSortingMode.ALPHABETICALLY;
                } else if (options.sorting.currentSortingMode == CurrentSortingMode.TAG) {
                    options.sorting.globalSortingMode = GlobalSortingMode.BY_TAG;
                } else if (options.sorting.currentSortingMode == CurrentSortingMode.COUNT_DESCENDING) {
                    options.sorting.globalSortingMode = GlobalSortingMode.DESCENDING;
                } else if (options.sorting.currentSortingMode == CurrentSortingMode.COUNT_ASCENDING) {
                    options.sorting.globalSortingMode = GlobalSortingMode.ASCENDING;
                } else if (options.sorting.currentSortingMode == CurrentSortingMode.CREATIVE_MENU) {
                    options.sorting.globalSortingMode = GlobalSortingMode.CREATIVE_MENU;
                }
            });
            ContainerHelper.storeActiveSortMode(nextMode);
            if (SORT_SOUND_COOLDOWN == 0) {
                SORT_SOUND_COOLDOWN = DEFAULT_SORT_SOUND_COOLDOWN;
                playSortSound(minecraft);
            }
        }
    }

    /**
     * Sets the move amount when scrolling.
     */
    public void setMoveAmount(Slot hoveredSlot, double mouseX, double mouseY, double scrollY) {
        if (!options().management.singularMoving) {
            return;
        }

        boolean validHoveredSlot = hoveredSlotHasItem(hoveredSlot) && hoveredSlot.getItem().getCount() > 1;
        if (!isCreativeInventoryScreen(screen) && instance().getCanMoveOne() && (validHoveredSlot && hasDropOnlyOneItemKeyDown())
                || buttonHoveredAndActive(managementButtons.quickDrop(), mouseX, mouseY) ? hasDropOnlyOneItemKeyDown()
                : ((validHoveredSlot || buttonHoveredAndActive(managementButtons.transferContainer(), mouseX, mouseY) || buttonHoveredAndActive(managementButtons.transferInventory(), mouseX, mouseY)) && hasMoveSingleModifierDown())) {
            MOVE_AMOUNT += (int)scrollY;
            if (MOVE_AMOUNT < 1) {
                MOVE_AMOUNT = 1;
            } else if (MOVE_AMOUNT > 64) {
                MOVE_AMOUNT = 64;
            } else if (hoveredSlotHasItem(hoveredSlot) && MOVE_AMOUNT > hoveredSlot.getItem().getMaxStackSize()) {
                MOVE_AMOUNT = hoveredSlot.getItem().getMaxStackSize();
            }
        }
    }

    @Override
    public QuesoScreen instance() {
        return (QuesoScreen) this.screen;
    }
}