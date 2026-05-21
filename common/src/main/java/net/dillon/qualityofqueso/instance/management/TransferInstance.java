package net.dillon.qualityofqueso.instance.management;

import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.dillon.qualityofqueso.instance.QuesoScreen;
import net.dillon.qualityofqueso.option.eum.management.FilteringMode;
import net.dillon.qualityofqueso.server.DedicatedServerStorage;
import net.dillon.qualityofqueso.widget.SwapButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.lwjgl.glfw.GLFW;

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
        boolean dropFromInventory = isInventoryScreen(instance().getScreen());
        moveItems(true, true, dropFromInventory, false);
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
        return options().management.scrollMoving && hasMoveSingleModifierDown();
    }

    /**
     * @return if the user can singular quick drop an item.
     */
    public boolean canSingularQuickDrop(int keycode) {
        return options().management.scrollMoving
                && keycode == key(getDropKey()).getValue()
                && hasDropOnlyOneItemKeyDown()
                && hoveredSlotHasItem(instance().getScreensHoveredSlot());
    }

    /**
     * Performs a singular drop on an item.
     */
    public void performSingularDrop() {
        int hoveredMenuSlotId = getMenuSlotId(instance().getScreensHoveredSlot());
        if (hoveredMenuSlotId == -1) {
            return;
        }
        for (int i = 0; i < MOVE_AMOUNT; i++) {
            sendClickSlotPacket(hoveredMenuSlotId, ClickType.THROW);
        }
    }

    /**
     * Transfers items from one container to another.
     */
    public void moveItems(boolean toInventory, boolean drop, boolean fromInventory, boolean canSingleMove) {
        int totalSlots = getTotalSlots();
        int playerStorageStart = findPlayerStorageStartIndex();
        int playerStorageEnd = findPlayerStorageEndIndexExclusive();
        int containerSize = getContainerSize();
        ItemStack hoveredDropFilter = drop && hoveredSlotHasItem(instance().getScreensHoveredSlot()) ? instance().getScreensHoveredSlot().getItem().copy() : ItemStack.EMPTY;

        int containerEnd = playerStorageStart != -1 ? playerStorageStart : Math.max(0, Math.min(containerSize, totalSlots));
        int effectivePlayerStart = playerStorageStart != -1 ? playerStorageStart : containerEnd;
        int effectivePlayerEnd = playerStorageEnd > effectivePlayerStart ? playerStorageEnd : totalSlots;

        int fromStart = toInventory ? 0 : effectivePlayerStart;
        int fromEnd = toInventory ? containerEnd : effectivePlayerEnd;
        int toStart = toInventory ? effectivePlayerStart : 0;
        int toEnd = toInventory ? effectivePlayerEnd : containerEnd;

        if (drop && fromInventory) {
            fromStart = effectivePlayerStart;
            fromEnd = effectivePlayerEnd;
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
            } else if (!drop && shouldApplyMatchingFilter() && !isPresent(toInventory, fromStack)) { // Skip items that aren't already present/filtered
                continue;
            } else if (drop && !hoveredDropFilter.isEmpty() && !fromSlot.getItem().is(hoveredDropFilter.getItem())) { // Only drop items matching the initially hovered item
                continue;
            }

            if (!fromStack.isEmpty()) {
                ItemStack cursorStack = getCursorStack();
                // Only transfer items if the query matches whatever the cursor is holding
                ClickType slotActionType = drop ? ClickType.THROW : ClickType.QUICK_MOVE;

                if (shouldFillStacksOnly() && !drop) {
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
     * @return the menu slot index.
     */
    private int getMenuSlotId(Slot slot) {
        return instance().getScreenMenu().slots.indexOf(slot);
    }

    /**
     * Moves one item from the currently hovered slot when clicking with the move-single modifier.
     *
     * @return {@code true} if the click was handled.
     */
    public boolean tryMoveSingleFromHovered(Slot hoveredSlot, int bl) {
        if ((bl != GLFW.GLFW_MOUSE_BUTTON_LEFT && bl != GLFW.GLFW_MOUSE_BUTTON_RIGHT)
                || !options().management.scrollMoving
                || !hasMoveSingleModifierDown()
                || instance().getScreensHoveredSlot() == null
                || !instance().getScreensHoveredSlot().hasItem()) {
            return false;
        }

        int totalSlots = getTotalSlots();
        int hoveredIndex = getMenuSlotId(hoveredSlot);
        if (hoveredIndex == -1) {
            return false;
        }
        boolean toContainer;
        boolean toInventory;
        int fromStart;
        int fromEnd;
        int toStart;
        int toEnd;

        if (isInventoryScreen(instance().getScreen())) {
            // Inventory screen layout:
            // 0 result, 1-4 crafting input, 5-8 armor, 9-35 inventory, 36-44 hotbar, 45 offhand.
            int playerStorageStart = 9;
            int playerStorageEnd = Math.min(totalSlots, 45);
            int craftingStart = 1;
            int craftingEnd = Math.min(totalSlots, 5);
            boolean hoveredInPlayerStorage = hoveredIndex >= playerStorageStart && hoveredIndex < playerStorageEnd;
            boolean hoveredInCrafting = hoveredIndex >= craftingStart && hoveredIndex < craftingEnd;

            if (!hoveredInPlayerStorage && !hoveredInCrafting) {
                return false;
            }

            toContainer = hoveredInPlayerStorage;
            toInventory = !toContainer;

            if (toContainer) {
                fromStart = playerStorageStart;
                fromEnd = playerStorageEnd;
                toStart = craftingStart;
                toEnd = craftingEnd;
            } else {
                fromStart = craftingStart;
                fromEnd = craftingEnd;
                toStart = playerStorageStart;
                toEnd = playerStorageEnd;
            }
        } else {
            int containerSize = getContainerBoundaryForSingleMove();
            if (containerSize <= 0) {
                return false;
            }

            boolean hoveredInContainer = hoveredIndex < containerSize;
            toContainer = !hoveredInContainer;
            toInventory = !toContainer;

            fromStart = toContainer ? containerSize : 0;
            fromEnd = toContainer ? totalSlots : containerSize;
            toStart = toContainer ? 0 : containerSize;
            toEnd = toContainer ? containerSize : totalSlots;
        }

        ItemStack targetItem = hoveredSlot.getItem();
        boolean allowEmptyTargets = true;
        int moveAmount = Math.max(1, MOVE_AMOUNT);

        if (hoveredIndex >= fromStart
                && hoveredIndex < fromEnd
                && canUseScrollSourceSlot(hoveredSlot, targetItem, toContainer, totalSlots)) {
            if (tryMoveIntoPreferredFurnaceFuelSlot(hoveredSlot, toContainer, targetItem, moveAmount)) {
                return true;
            }

            return moveSingleFromSourceSlot(
                    hoveredSlot,
                    toStart,
                    toEnd,
                    moveAmount,
                    toInventory,
                    targetItem,
                    !getCursorStack().isEmpty(),
                    allowEmptyTargets,
                    true
            );
        }

        for (int i = fromStart; i < fromEnd; i++) {
            Slot sourceSlot = instance().getScreenMenu().getSlot(i);
            if (getMenuSlotId(sourceSlot) == hoveredIndex || !canUseScrollSourceSlot(sourceSlot, targetItem, toContainer, totalSlots)) {
                continue;
            }

            if (tryMoveIntoPreferredFurnaceFuelSlot(sourceSlot, toContainer, targetItem, moveAmount)) {
                return true;
            }

            return moveSingleFromSourceSlot(
                    sourceSlot,
                    toStart,
                    toEnd,
                    moveAmount,
                    toInventory,
                    targetItem,
                    !getCursorStack().isEmpty(),
                    allowEmptyTargets,
                    true
            );
        }
        return false;
    }

    /**
     * Tries to move into the furnace fuel slot first when applicable.
     */
    private boolean tryMoveIntoPreferredFurnaceFuelSlot(Slot sourceSlot, boolean toContainer, ItemStack targetItem, int amount) {
        if (!toContainer || !(instance().getScreen() instanceof AbstractFurnaceScreen<?> furnaceScreen)) {
            return false;
        }

        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return false;
        }

        if (!AbstractFurnaceBlockEntity.isFuel(sourceSlot.getItem())) {
            return false;
        }

        // Furnace slot layout: 0=input, 1=fuel, 2=result.
        return moveSingleFromSourceSlot(
                sourceSlot,
                1,
                2,
                amount,
                false,
                targetItem,
                !getCursorStack().isEmpty(),
                true,
                true
        );
    }

    /**
     * @return the container/player boundary index used for single-item moving.
     */
    private int getContainerBoundaryForSingleMove() {
        int containerSize = getContainerSize();
        if (containerSize > 0) {
            return containerSize;
        }

        int playerStorageStart = findPlayerStorageStartIndex();
        if (playerStorageStart > 0) {
            return playerStorageStart;
        }

        return isCraftingScreen(instance().getScreen()) ? 5 : 0;
    }

    /**
     * @return the first screen slot index that belongs to player storage (main inventory or hotbar).
     */
    private int findPlayerStorageStartIndex() {
        if (instance().getMinecraft().player == null) {
            return -1;
        }

        Inventory playerInventory = instance().getMinecraft().player.getInventory();
        int firstPlayerStorageSlot = Integer.MAX_VALUE;

        for (Slot slot : instance().getScreenMenu().slots) {
            if (slot.container != playerInventory) {
                continue;
            }

            int playerSlot = slot.getContainerSlot();
            if (playerSlot >= 0 && playerSlot <= 35) {
                firstPlayerStorageSlot = Math.min(firstPlayerStorageSlot, getMenuSlotId(slot));
            }
        }

        return firstPlayerStorageSlot == Integer.MAX_VALUE ? -1 : firstPlayerStorageSlot;
    }

    /**
     * @return if the user can use singular moving.
     */
    private boolean canUseScrollSourceSlot(Slot sourceSlot, ItemStack targetItem, boolean toContainer, int totalSlots) {
        if (!sourceSlot.hasItem()) {
            return false;
        }
        if (!ItemStack.isSameItemSameTags(sourceSlot.getItem(), targetItem)) {
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
        return moveSingleFromSourceSlot(sourceSlot, toStart, toEnd, amount, toInventory, filterStack, preserveCarriedStack, allowEmptyTargets, false);
    }

    /**
     * Moves one item from a single source slot into the target range.
     *
     * @param ignoreContainerFiltering if true, skip tracked-container filter restrictions.
     *
     * @return {@code true} if one item was moved.
     */
    public boolean moveSingleFromSourceSlot(Slot sourceSlot, int toStart, int toEnd, int amount, boolean toInventory, ItemStack filterStack, boolean preserveCarriedStack, boolean allowEmptyTargets, boolean ignoreContainerFiltering) {
        if (sourceSlot == null || !sourceSlot.hasItem()) {
            return false;
        }

        int parkedCursorSlot = -1;
        if (preserveCarriedStack && !getCursorStack().isEmpty()) {
            int parkedCursorSlotIndex = findTemporaryEmptySlotForCursor();
            if (parkedCursorSlotIndex == -1) {
                return false;
            }

            Slot parkedSlot = instance().getScreenMenu().getSlot(parkedCursorSlotIndex);
            parkedCursorSlot = getMenuSlotId(parkedSlot);
            if (parkedCursorSlot == -1) {
                return false;
            }

            performClickSlot(instance().getScreen(), parkedSlot, parkedCursorSlot, 0, ClickType.PICKUP);
        }

        try {
            if (!filterStack.isEmpty() && !ItemStack.isSameItemSameTags(sourceSlot.getItem(), filterStack)) {
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
                if (getMenuSlotId(target) == parkedCursorSlot) {
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
                if (quickTransferSingle(emptySlots, nonEmptySlots, sourceSlot, ignoreContainerFiltering)) {
                    movedOne = true;
                }
            }
            return movedOne;
        } finally {
            if (parkedCursorSlot != -1) {
                // If a move path left something on the cursor, attempt to return it to source first.
                if (!instance().getScreenMenu().getCarried().isEmpty()) {
                    int sourceMenuSlotId = getMenuSlotId(sourceSlot);
                    if (sourceMenuSlotId != -1) {
                        Slot freshSourceSlot = instance().getScreenMenu().getSlot(sourceMenuSlotId);
                        performClickSlot(instance().getScreen(), freshSourceSlot, sourceMenuSlotId, 0, ClickType.PICKUP);
                    }
                }

                // Restore the originally parked carried stack when possible.
                if (instance().getScreenMenu().getCarried().isEmpty()) {
                    Slot parkedSlot = instance().getScreenMenu().getSlot(parkedCursorSlot);
                    if (parkedSlot.hasItem()) {
                        performClickSlot(instance().getScreen(), parkedSlot, parkedCursorSlot, 0, ClickType.PICKUP);
                    }
                }
            }
        }
    }

    /**
     * @return the index after the last screen slot that belongs to player storage (main inventory or hotbar).
     */
    private int findPlayerStorageEndIndexExclusive() {
        if (instance().getMinecraft().player == null) {
            return -1;
        }

        Inventory playerInventory = instance().getMinecraft().player.getInventory();
        int lastPlayerStorageSlot = -1;

        for (Slot slot : instance().getScreenMenu().slots) {
            if (slot.container != playerInventory) {
                continue;
            }

            int playerSlot = slot.getContainerSlot();
            if (playerSlot >= 0 && playerSlot <= 35) {
                lastPlayerStorageSlot = Math.max(lastPlayerStorageSlot, getMenuSlotId(slot));
            }
        }

        return lastPlayerStorageSlot == -1 ? -1 : lastPlayerStorageSlot + 1;
    }

    /**
     * Quickly moves {@code one item} from the {@code source slot.}
     *
     * @param ignoreContainerFiltering if true, skip tracked-container filter restrictions.
     *
     * @return {@code true} if exactly one item was moved from the source slot.
     */
    public boolean quickTransferSingle(Deque<Slot> emptySlots, List<Slot> nonEmptySlots, Slot sourceSlot, boolean ignoreContainerFiltering) {
        ItemStack original = sourceSlot.getItem();
        if (original.isEmpty()) {
            return false;
        }

        if (!instance().getScreenMenu().getCarried().isEmpty()) {
            return false;
        }

        int slotId = getMenuSlotId(sourceSlot);
        if (slotId == -1) {
            return false;
        }

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

            if (!target.mayPlace(carried)) {
                continue;
            }

            if (!ignoreContainerFiltering
                    && ContainerHelper.IS_TRACKED_CONTAINER
                    && options().management.containerFiltering
                    && ContainerHelper.CURRENT_FILTER_MODE == FilteringMode.CURRENT_STACKS
                    && !itemMatchesPlaceholder(target.getItem())) {
                continue;
            }

            if (!ItemStack.isSameItemSameTags(carried, target.getItem())) {
                continue;
            }

            int max = Math.min(target.getMaxStackSize(), target.getMaxStackSize(target.getItem()));
            if (target.getItem().getCount() >= max) {
                continue;
            }

            // Right-click places 1 item
            int targetMenuSlotId = getMenuSlotId(target);
            if (targetMenuSlotId == -1) {
                continue;
            }
            performClickSlot(instance().getScreen(), target, targetMenuSlotId, 1, ClickType.PICKUP);

            movedOne = true;
            // Only move one item total per source sourceSlot
            break;
        }

        // STEP 3: try empty slots if nothing stacked
        if (!movedOne) {
            for (Iterator<Slot> it = emptySlots.iterator(); it.hasNext();) {
                Slot empty = it.next();

                if (!empty.mayPlace(carried)) {
                    continue;
                }

                int emptyMenuSlotId = getMenuSlotId(empty);
                if (emptyMenuSlotId == -1) {
                    continue;
                }
                performClickSlot(instance().getScreen(), empty, emptyMenuSlotId, 1, ClickType.PICKUP);

                if (empty.hasItem()) {
                    nonEmptySlots.add(empty);
                    it.remove();
                    movedOne = true;
                    break;
                }
            }
        }

        // Return leftovers to original sourceSlot
        if (!instance().getScreenMenu().getCarried().isEmpty()) {
            Slot freshSourceSlot = instance().getScreenMenu().getSlot(slotId);
            performClickSlot(instance().getScreen(), freshSourceSlot, slotId, 0, ClickType.PICKUP);

            // Defensive second pass: in edge cases (e.g., transient slot mutation), first restore can fail.
            if (!instance().getScreenMenu().getCarried().isEmpty()) {
                freshSourceSlot = instance().getScreenMenu().getSlot(slotId);
                performClickSlot(instance().getScreen(), freshSourceSlot, slotId, 0, ClickType.PICKUP);
            }
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

            int playerMenuSlotId = getMenuSlotId(playerSlot);
            int chestMenuSlotId = getMenuSlotId(chestSlot);
            if (playerMenuSlotId == -1 || chestMenuSlotId == -1) {
                continue;
            }

            sendSwapSlotPacket(playerMenuSlotId, chestMenuSlotId);
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
