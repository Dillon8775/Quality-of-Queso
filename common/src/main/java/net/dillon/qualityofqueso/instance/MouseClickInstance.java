package net.dillon.qualityofqueso.instance;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.qualityofqueso.instance.management.CursorKey;
import net.dillon.qualityofqueso.instance.management.ManagementInstance;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.ModConstants.MOVE_AMOUNT;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.*;
import static net.dillon.qualityofqueso.instance.management.ExtractingInstance.setCursor;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Handles mouse-clicking events.
 */
public class MouseClickInstance extends ManagementInstance {

    public MouseClickInstance(QuesoScreen screen) {
        super(screen);
    }

    /**
     * Attempts to lock or select a slot when clicking.
     */
    public void trySelectingOrLockingSlot(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (isExcludingOrLockingSlots() && hasAttemptedToLockSelectOrDeselect(event)) {
            lockedSlotsInstance().selectOrLockSlot(event, cir);
        }
    }

    /**
     * Quickly equips an item.
     */
    public void quickEquipItem(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT && instance().getScreensHoveredSlot() != null && quickEquipInstance().isQuicklyEquippable(instance().getScreensHoveredSlot().getItem())) {
            quickEquipInstance().quickEquipItem();
            cir.setReturnValue(true);
        }
    }

    /**
     * Refocus fromInventory search field if clicked.
     */
    public void handleInventorySearchFieldClicking(MouseButtonEvent event, boolean doubleClick) {
        if (instance().getSearchFields().inventory() != null && instance().getSearchFields().inventory().mouseClicked(event, doubleClick)) {
            instance().getSearchFields().inventory().setFocused(true);
        }
    }

    /**
     * Handles clicking inactive buttons.
     */
    public void handleButtonInactiveSounds() {
        if (buttonHoveredButInactive(instance().getManagementButtons().transferContainer())
                || buttonHoveredButInactive(instance().getManagementButtons().transferInventory())
                || buttonHoveredButInactive(instance().getManagementButtons().quickDrop())
                || buttonHoveredButInactive(instance().getManagementButtons().sort())
                || buttonHoveredButInactive(instance().getManagementButtons().swap())
        ) {
            playButtonInactiveSound(instance().getMinecraft());
        }
    }

    /**
     * Handles moving only one or dropping one item in a stack.
     */
    public void moveOnlyOne(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (client().management().scrollMoving) {
            boolean dropOnlyOne = hasDropOnlyOneItemModifierDown();
            boolean hasSingleModifierDown = canScrollMoveAndHasScrollModifierDown();
            if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT && ((((dropOnlyOne || hasSingleModifierDown) && hoveredSlotHasItem(instance().getScreensHoveredSlot())))
                    || buttonHoveredAndActive(instance().getManagementButtons().transferInventory())
                    || buttonHoveredAndActive(instance().getManagementButtons().transferContainer()))) {
                MOVE_AMOUNT = 1;
                cir.setReturnValue(true);
            }
        }
    }

    /**
     * Attempts to quick move any highlighted or similar items related to the cursor/hovered stack.
     */
    public void tryQuickMoveHighlightedItems(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (!(isContainerScreen(instance().getScreen()) || isDropperDispenserOrHopperScreen(instance().getScreen())) || !event.hasControlDown() || event.button() != InputConstants.MOUSE_BUTTON_LEFT) {
            return;
        }

        // Get common variables
        AbstractContainerMenu menu = instance().getScreenMenu();
        Slot hoveredSlot = instance().getScreensHoveredSlot();

        // Ensure hovered slot isn't null
        if (hoveredSlot == null) {
            return;
        }

        // Ctrl-clicking while holding something on the cursor does nothing
        if (!getCursorStack().isEmpty()) {
            return;
        }

        ItemStack hoveredStack = hoveredSlot.getItem();
        if (hoveredStack.isEmpty()) {
            return;
        }

        // true = inventory -> container
        // false = container -> inventory
        boolean fromInventory = isPlayerInventorySlot(hoveredSlot);
        Item targetItem = hoveredStack.getItem();

        for (int i = 0; i < getTotalSlots(); i++) {
            Slot slot = menu.getSlot(i);
            ItemStack stack = slot.getItem();

            // Ensure we aren't moving empty or duplicate stacks
            if (stack.isEmpty() || !stack.is(targetItem)) {
                continue;
            }

            // Only move items from the same side as the hovered slot
            if (isPlayerInventorySlot(slot) != fromInventory) {
                continue;
            }

            // Move items, as long as its not locked
            if (!lockedSlotsInstance().isLockedSlot(slot.index)) {
                instance().getMinecraft().gameMode.handleContainerInput(
                        menu.containerId,
                        slot.index,
                        0,
                        ContainerInput.QUICK_MOVE,
                        instance().getMinecraft().player
                );
            }
        }

        // Return true for mouse clicked
        setCursor(CursorKey.MOVE);
        cir.setReturnValue(true);
    }
}