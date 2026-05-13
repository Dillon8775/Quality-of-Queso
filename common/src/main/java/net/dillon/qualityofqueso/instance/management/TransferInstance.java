package net.dillon.qualityofqueso.instance.management;

import net.dillon.qualityofqueso.instance.QuesoScreen;
import net.dillon.qualityofqueso.server.DedicatedServerStorage;
import net.dillon.qualityofqueso.widget.SwapButton;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.*;

import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.MethodHelper.key;
import static net.dillon.qualityofqueso.helper.MethodHelper.performClickSlot;
import static net.dillon.qualityofqueso.helper.ModHelper.options;
import static net.dillon.qualityofqueso.keybind.ModKeybinds.*;
import static net.dillon.qualityofqueso.util.ModConstants.MOVE_AMOUNT;

/**
 * Handles transferring/basic management related things.
 */
public class TransferInstance extends ManagementInstance {

    public TransferInstance(QuesoScreen screen) {
        super(screen);
    }

    /**
     * Helper method to drop all selected items in the respective inventory/container.
     */
    public void dropItems(boolean fromInventory) {
        moveItems(true, true, fromInventory, false);
    }

    /**
     * Helper method to transfer all selected items in the respective inventory/container.
     */
    public void transferItems(boolean toInventory, boolean canSingleMove) {
        moveItems(toInventory, false, false, canSingleMove);
    }

    /**
     * @return if the user can single move an item.
     */
    public boolean canSingularMove() {
        return options().management.singularMoving && hasMoveSingleModifierDown();
    }

    /**
     * @return if the user can singular quick drop an item.
     */
    public boolean canSingularQuickDrop(int keycode) {
        return options().management.singularMoving
                && keycode == key(getDropKey()).getValue()
                && hasDropOnlyOneItemKeyDown()
                && hoveredSlotHasItem(instance().getScreensHoveredSlot());
    }

    /**
     * Performs a singular drop on an item.
     */
    public void performSingularDrop() {
        for (int i = 0; i < MOVE_AMOUNT; i++) {
            sendClickSlotPacket(instance().getScreensHoveredSlot().index, ClickType.THROW);
        }
    }

    /**
     * Transfers items from one container to another.
     */
    public void moveItems(boolean toInventory, boolean drop, boolean fromInventory, boolean canSingleMove) {
        int containerSize = getContainerSize();
        int totalSlots = getTotalSlots();
        ItemStack hoveredDropFilter = drop && hoveredSlotHasItem(instance().getScreensHoveredSlot()) ? instance().getScreensHoveredSlot().getItem().copy() : ItemStack.EMPTY;

        int fromStart = toInventory ? 0 : containerSize;
        int fromEnd = toInventory ? containerSize : totalSlots;
        int toStart = toInventory ? containerSize : 0;
        int toEnd = toInventory ? totalSlots : containerSize;

        if (drop && fromInventory) {
            fromStart = 9;
            fromEnd = 45;
        }

        if (isBrewingStandScreen(instance().getScreen())) {
            fromStart = 0;
            fromEnd = 3;
        }

        if (isFurnaceScreen(instance().getScreen())) {
            fromStart = 2;
            fromEnd = 3;
        }

        boolean movedItem = false;
        boolean singleMoveMode = canSingleMove && !drop && hasMoveSingleModifierDown();
        ItemStack cursorFilterStack = getCursorStack().copy();
        boolean canSingleWithCursor = cursorFilterStack.isEmpty() || findTemporaryEmptySlotForCursor() != -1;

        // Normal logic (dropping and quick move)
        for (int i = fromStart; i < fromEnd; i++) {
            Slot fromSlot = instance().getScreenMenu().getSlot(i);
            ItemStack fromStack = fromSlot.getItem();

            // Skip player-chosen excluded slots
            if (isExcludedSlot(fromSlot.index)) {
                continue;
            }

            if (searchInstance().isFilteredBySearch(fromSlot, false)) {
                continue; // Then skip container slot if query not found via search
            } else if (!options().management.includeHotbar) {
                if (drop) {
                    if (isExcludedInventorySlot(fromSlot.index) || isInventoryHotbarSlot(isInventoryScreen(instance().getScreen()), fromSlot.index)) {
                        continue; // If dropping from InventoryScreen, and it's an excluded slot AND fromInventory hotbar slot, skip slot and continue
                    }
                } else if (!toInventory && isHotbarSlot(fromEnd, fromSlot.index)) {
                    continue; // Otherwise, check if it's a hotbar slot in normal container
                }
            }

            if (options().lockedSlots.enableLockedSlots && lockedSlotsInstance().isLockedSlot(fromSlot.index)) { // Skip locked slots (always)
                continue;
            } else if (!drop && options().management.moveMatchingItems && !isPresent(toInventory, fromStack)) { // Skip items that aren't already present/filtered
                continue;
            } else if (drop && !hoveredDropFilter.isEmpty() && !fromSlot.getItem().is(hoveredDropFilter.getItem())) { // Only drop items matching the initially hovered item
                continue;
            }

            if (!fromStack.isEmpty()) {
                ItemStack cursorStack = getCursorStack();
                // Only transfer items if the query matches whatever the cursor is holding
                ClickType slotActionType = drop ? ClickType.THROW : ClickType.QUICK_MOVE;

                if (options().isFillStacksEnabled() && options().buttonDisplayOptions.displayFillStacks && !drop) {
                    if (!cursorStack.isEmpty() && !canMoveCursorItem(fromSlot, false, toInventory)) {
                        continue;
                    }

                    int movableAmount = Math.min(fromStack.getCount(), getDestinationFreeSpaceForStack(fromStack, toStart, toEnd));
                    if (movableAmount <= 0) {
                        continue;
                    }

                    if (moveSingleFromSourceSlot(
                            fromSlot,
                            toStart,
                            toEnd,
                            movableAmount,
                            toInventory,
                            cursorStack,
                            !cursorStack.isEmpty(),
                            false
                    )) {
                        movedItem = true;
                    }
                    continue;
                }

                if (singleMoveMode && canSingleWithCursor) {
                    if (moveSingleFromSourceSlot(
                            fromSlot,
                            toStart,
                            toEnd,
                            MOVE_AMOUNT,
                            toInventory,
                            cursorFilterStack,
                            !cursorFilterStack.isEmpty(),
                            true
                    )) {
                        movedItem = true;
                    }
                } else if (!cursorStack.isEmpty()) {
                    if (canMoveCursorItem(fromSlot, false, toInventory)) {
                        sendClickSlotPacket(i, slotActionType);
                        movedItem = true;
                    }
                }
                // If cursor has nothing in it, move all items over
                else {
                    for (int k = 0; k < (drop ? MOVE_AMOUNT : 1); k++) {
                        sendClickSlotPacket(i, slotActionType);
                    }
                    movedItem = true;
                }
            }
        }

        if (movedItem) {
            playButtonSound(instance().getMinecraft(), drop);
        } else {
            playButtonInactiveSound(instance().getMinecraft());
        }
    }

    /**
     * Moves one item matching the hovered stack based on scroll direction.
     *
     * <p>Scrolling up transfers from inventory to container.
     * Scrolling down transfers from container to inventory.</p>
     *
     * @return {@code true} if the scroll was handled.
     */
    public boolean tryMoveSingleFromScroll(Slot hoveredSlot, double scrollY) {
        if (!options().management.singularMoving
                || hoveredSlot == null
                || !hoveredSlot.hasItem()
                || scrollY == 0
                || getContainerSize() <= 0) {
            return false;
        }

        boolean toContainer = scrollY > 0;
        boolean toInventory = !toContainer;
        int containerSize = getContainerSize();
        int totalSlots = getTotalSlots();
        int fromStart = toContainer ? containerSize : 0;
        int fromEnd = toContainer ? totalSlots : containerSize;
        int toStart = toContainer ? 0 : containerSize;
        int toEnd = toContainer ? containerSize : totalSlots;
        ItemStack targetItem = hoveredSlot.getItem();
        int hoveredIndex = hoveredSlot.index;

        if (hoveredIndex >= fromStart
                && hoveredIndex < fromEnd
                && canUseScrollSourceSlot(hoveredSlot, targetItem, toContainer, totalSlots)) {
            return moveSingleFromSourceSlot(
                    hoveredSlot,
                    toStart,
                    toEnd,
                    1,
                    toInventory,
                    targetItem,
                    !getCursorStack().isEmpty(),
                    !options().isFillStacksEnabled()
            );
        }

        for (int i = fromStart; i < fromEnd; i++) {
            Slot sourceSlot = instance().getScreenMenu().getSlot(i);
            if (sourceSlot.index == hoveredIndex || !canUseScrollSourceSlot(sourceSlot, targetItem, toContainer, totalSlots)) {
                continue;
            }

            return moveSingleFromSourceSlot(
                    sourceSlot,
                    toStart,
                    toEnd,
                    1,
                    toInventory,
                    targetItem,
                    !getCursorStack().isEmpty(),
                    !options().isFillStacksEnabled()
            );
        }
        return false;
    }

    /**
     * @return if the user can use singular moving.
     */
    private boolean canUseScrollSourceSlot(Slot sourceSlot, ItemStack targetItem, boolean toContainer, int totalSlots) {
        if (!sourceSlot.hasItem()) {
            return false;
        }
        if (!ItemStack.isSameItemSameComponents(sourceSlot.getItem(), targetItem)) {
            return false;
        }
        if (isExcludedSlot(sourceSlot.index)) {
            return false;
        }
        if (options().lockedSlots.enableLockedSlots && options().lockedSlots.hardLockSlots && lockedSlotsInstance().isLockedSlot(sourceSlot.index)) {
            return false;
        }
        if (toContainer && !options().management.includeHotbar && isHotbarSlot(totalSlots, sourceSlot.index)) {
            return false;
        }
        return !searchInstance().isFilteredBySearch(sourceSlot, false);
    }

    /**
     * Moves one item from a single source slot into the target range.
     *
     * @param filterStack if non-empty, source stack must match this stack.
     * @param preserveCarriedStack if true, carried stack is parked/restored around the move.
     * @param allowEmptyTargets if false, the move only stacks into existing destination stacks.
     *
     * @return {@code true} if one item was moved.
     */
    public boolean moveSingleFromSourceSlot(Slot sourceSlot, int toStart, int toEnd, int amount, boolean toInventory, ItemStack filterStack, boolean preserveCarriedStack, boolean allowEmptyTargets) {
        if (sourceSlot == null || !sourceSlot.hasItem()) {
            return false;
        }

        int parkedCursorSlot = -1;
        if (preserveCarriedStack && !getCursorStack().isEmpty()) {
            parkedCursorSlot = findTemporaryEmptySlotForCursor();
            if (parkedCursorSlot == -1) {
                return false;
            }

            Slot parkedSlot = instance().getScreenMenu().getSlot(parkedCursorSlot);
            performClickSlot(instance().getScreen(), parkedSlot, parkedCursorSlot, 0, ClickType.PICKUP);
        }

        try {
            if (!filterStack.isEmpty() && !ItemStack.isSameItemSameComponents(sourceSlot.getItem(), filterStack)) {
                return false;
            }

            Deque<Slot> emptySlots = new ArrayDeque<>();
            List<Slot> nonEmptySlots = new ArrayList<>();
            LocalPlayer player = instance().getMinecraft().player;
            boolean iterateBackwards = (!DedicatedServerStorage.shouldUsePerpendicularQuickMoving(player.getUUID())) && toInventory;
            int step = iterateBackwards ? -1 : 1;
            int start = iterateBackwards ? toEnd - 1 : toStart;
            int endExclusive = iterateBackwards ? toStart - 1 : toEnd;

            for (int i = start; i != endExclusive; i += step) {
                Slot target = instance().getScreenMenu().getSlot(i);
                if (target.index == parkedCursorSlot) {
                    continue;
                }
                if (target.hasItem()) {
                    nonEmptySlots.add(target);
                } else if (allowEmptyTargets) {
                    emptySlots.add(target);
                }
            }

            boolean movedOne = false;
            for (int i = 0; i < amount; i++) {
                if (quickTransferSingle(emptySlots, nonEmptySlots, sourceSlot)) {
                    movedOne = true;
                }
            }
            return movedOne;
        } finally {
            if (parkedCursorSlot != -1 && instance().getScreenMenu().getCarried().isEmpty()) {
                Slot parkedSlot = instance().getScreenMenu().getSlot(parkedCursorSlot);
                if (parkedSlot.hasItem()) {
                    performClickSlot(instance().getScreen(), parkedSlot, parkedCursorSlot, 0, ClickType.PICKUP);
                }
            }
        }
    }

    /**
     * Quickly moves {@code one item} from the {@code source slot.}
     *
     * @return {@code true} if exactly one item was moved from the source slot.
     */
    public boolean quickTransferSingle(Deque<Slot> emptySlots, List<Slot> nonEmptySlots, Slot sourceSlot) {
        ItemStack original = sourceSlot.getItem();
        if (original.isEmpty()) {
            return false;
        }

        if (!instance().getScreenMenu().getCarried().isEmpty()) {
            return false;
        }

        int slotId = sourceSlot.index;

        // Pick up full stack
        performClickSlot(instance().getScreen(), sourceSlot, slotId, 0, ClickType.PICKUP);

        ItemStack carried = instance().getScreenMenu().getCarried();
        if (carried.isEmpty()) {
            return false;
        }

        boolean movedOne = false;

        // Try stacking into existing stacks first (1 item only)
        for (Slot target : nonEmptySlots) {
            if (carried.isEmpty()) {
                break;
            }

            if (!ItemStack.isSameItemSameComponents(carried, target.getItem())) {
                continue;
            }

            int max = Math.min(target.getMaxStackSize(), target.getMaxStackSize(target.getItem()));
            if (target.getItem().getCount() >= max) {
                continue;
            }

            // Right-click places 1 item
            performClickSlot(instance().getScreen(), target, target.index, 1, ClickType.PICKUP);

            movedOne = true;
            // Only move one item total per source sourceSlot
            break;
        }

        // STEP 3: try empty slots if nothing stacked
        if (!movedOne) {
            for (Iterator<Slot> it = emptySlots.iterator(); it.hasNext();) {
                Slot empty = it.next();

                performClickSlot(instance().getScreen(), empty, empty.index, 1, ClickType.PICKUP);

                if (empty.hasItem()) {
                    nonEmptySlots.add(empty);
                    it.remove();
                    movedOne = true;
                }

                break;
            }
        }

        // Return leftovers to original sourceSlot
        if (!instance().getScreenMenu().getCarried().isEmpty()) {
            performClickSlot(instance().getScreen(), sourceSlot, slotId, 0, ClickType.PICKUP);
        }

        return movedOne;
    }

    /**
     * Calls {@link ManagementInstance#clickSlot(int)} to sort items.
     */
    public void swapSlots(int a, int b, List<Integer> sortableSlots) {
        AbstractContainerMenu menu = instance().getScreenMenu();
        ItemStack aStack = menu.slots.get(a).getItem();
        ItemStack bStack = menu.slots.get(b).getItem();

        // For same-item swaps, click order matters when one side is full.
        // If we click a full destination second, the swap can no-op.
        if (canCombine(aStack, bStack)) {
            int bMax = Math.min(bStack.getMaxStackSize(), menu.slots.get(b).getMaxStackSize(bStack));
            if (bStack.getCount() >= bMax) {
                // Reverse order: pick B first, merge into A, then place remainder to B.
                clickSlot(b);
                clickSlot(a);
                clickSlot(b);
                return;
            }
        }

        // Pick up A
        clickSlot(a);
        // Pick up B (places A, picks up B)
        clickSlot(b);
        // Place B into A
        clickSlot(a);
    }

    /**
     * Swaps all items in a container.
     */
    public void swapItems() {
        AbstractContainerMenu menu = instance().getScreenMenu();
        int totalSlots = getTotalSlots();
        int containerSize = getContainerSize();
        int offset = totalSlots - 27 - 9;

        int swaps = Math.min(totalSlots - containerSize, containerSize);

        boolean swappedItem = false;
        for (int i = 0; i < swaps; i++) {
            int playerSlotIndex = i + offset;

            // Make sure both exist
            if (i >= totalSlots || playerSlotIndex >= totalSlots) {
                continue;
            }

            Slot chestSlot = menu.getSlot(i);
            Slot playerSlot = menu.getSlot(playerSlotIndex);

            // Skip player-chosen excluded slots
            boolean skip = false;
            for (int id : instance().getExcludedSlots()) {
                if (chestSlot.index == id || playerSlot.index == id) {
                    skip = true;
                    break;
                }
            }
            // Skip locked slots
            if (lockedSlotsInstance().isLockedSlot(chestSlot.index) || lockedSlotsInstance().isLockedSlot(playerSlot.index)) {
                continue;
            }
            if (skip) {
                continue;
            }

            if (!options().management.includeHotbar && isHotbarSlot(menu.slots.size(), playerSlot.index)) {
                continue;
            }

            ItemStack chestStack = chestSlot.getItem();
            ItemStack playerStack = playerSlot.getItem();

            // Ignore if both slots are empty
            if (chestStack.isEmpty() && playerStack.isEmpty()) {
                continue;
            }

            sendSwapSlotPacket(playerSlot.index, chestSlot.index);
            swappedItem = true;
        }

        if (swappedItem) {
            playButtonSound(instance().getMinecraft(), false);
        } else {
            playButtonInactiveSound(instance().getMinecraft());
        }
    }

    /**
     * @return if a container is able to swap items with the player inventory.
     */
    public boolean canSwap() {
        Inventory playerInventory = instance().getMinecraft().player.getInventory();
        return SwapButton.SWAP_COOLDOWN == 0 && (getContainerSize() != 27 || isAnySlotFilled(false, 27, 54))
                && getCursorStack().isEmpty()
                && instance().getSearchFields().searchText().isEmpty()
                && shouldButtonBeActive(false, null)
                && shouldButtonBeActive(true, playerInventory);
    }

    /**
     * Attempts to swap items.
     */
    public void trySwap() {
        if (SwapButton.SWAP_COOLDOWN == 0 && canSwap()) {
            swapItems();
            playButtonSound(instance().getMinecraft(), false);
            SwapButton.resetCooldown();
        } else {
            playButtonInactiveSound(instance().getMinecraft());
        }
    }
}