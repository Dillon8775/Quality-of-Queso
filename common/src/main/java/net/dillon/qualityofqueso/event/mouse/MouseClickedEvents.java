package net.dillon.qualityofqueso.event.mouse;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.qualityofqueso.event.QuesoScreen;
import net.dillon.qualityofqueso.event.management.CursorKey;
import net.dillon.qualityofqueso.event.management.ManagementEvents;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.event.management.ExtractingEvents.setCursor;
import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.ModConstants.MOVE_AMOUNT;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.*;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Handles mouse-clicking events.
 */
public class MouseClickedEvents extends ManagementEvents {

    public MouseClickedEvents(QuesoScreen screen) {
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
        if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT && holder().screensHoveredSlot() != null && quickEquipInstance().isQuicklyEquippable(holder().screensHoveredSlot().getItem())) {
            quickEquipInstance().quickEquipItem();
            cir.setReturnValue(true);
        }
    }

    /**
     * Refocus fromInventory search field if clicked.
     */
    public void handleInventorySearchFieldClicking(MouseButtonEvent event, boolean doubleClick) {
        if (holder().searchFields().inventory() != null && holder().searchFields().inventory().mouseClicked(event, doubleClick)) {
            holder().searchFields().inventory().setFocused(true);
        }
    }

    /**
     * Handles clicking inactive buttons.
     */
    public void handleButtonInactiveSounds() {
        if (buttonHoveredButInactive(holder().managementButtons().transferContainer())
                || buttonHoveredButInactive(holder().managementButtons().transferInventory())
                || buttonHoveredButInactive(holder().managementButtons().quickDrop())
                || buttonHoveredButInactive(holder().managementButtons().sort())
                || buttonHoveredButInactive(holder().managementButtons().swap())
        ) {
            playButtonInactiveSound();
        }
    }

    /**
     * Handles moving only one or dropping one item in a stack.
     */
    public void moveOnlyOne(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (client().management().scrollMoving) {
            boolean dropOnlyOne = hasDropOnlyOneItemModifierDown();
            boolean hasSingleModifierDown = canScrollMoveAndHasScrollModifierDown();
            if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT && ((((dropOnlyOne || hasSingleModifierDown) && hoveredSlotHasItem(holder().screensHoveredSlot())))
                    || buttonHoveredAndActive(holder().managementButtons().transferInventory())
                    || buttonHoveredAndActive(holder().managementButtons().transferContainer()))) {
                MOVE_AMOUNT = 1;
                cir.setReturnValue(true);
            }
        }
    }

    /**
     * @return the current count in a slot.
     */
    private int getSlotCount(Slot slot) {
        return slot.getItem().getCount();
    }

    /**
     * Attempts to quick move any highlighted or similar items related to the cursor/hovered stack.
     */
    public void tryQuickMoveHighlightedItems(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (!(isContainerScreen(holder().screen()) || isDropperDispenserOrHopperScreen(holder().screen())) || !event.hasControlDown() || event.button() != InputConstants.MOUSE_BUTTON_LEFT) {
            return;
        }

        // Get common variables
        AbstractContainerMenu menu = holder().menu();
        Slot hoveredSlot = holder().screensHoveredSlot();

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

        // Track if any item was moved
        boolean moved = false;

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
                int slotCount = getSlotCount(slot);
                holder().mc().gameMode.handleContainerInput(
                        menu.containerId,
                        slot.index,
                        0,
                        ContainerInput.QUICK_MOVE,
                        holder().mc().player
                );

                // Item was moved if slot count is different
                if (slotCount != getSlotCount(slot)) {
                    moved = true;
                }
            }
        }

        // Return true for mouse clicked
        setCursor(CursorKey.MOVE);

        // Play sound if moved
        if (moved) {
            playSortSound();
        }

        cir.setReturnValue(true);
    }
}