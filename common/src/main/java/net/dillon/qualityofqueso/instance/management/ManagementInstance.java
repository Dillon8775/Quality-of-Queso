package net.dillon.qualityofqueso.instance.management;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.dillon.qualityofqueso.helper.ContainerHelper;
import net.dillon.qualityofqueso.instance.ModInstance;
import net.dillon.qualityofqueso.instance.QuesoScreen;
import net.dillon.qualityofqueso.instance.WidgetHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.inventory.BrewingStandScreen;
import net.minecraft.client.gui.screens.inventory.DispenserScreen;
import net.minecraft.client.gui.screens.inventory.HopperScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.HashedPatchMap;
import net.minecraft.network.HashedStack;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.MethodHelper.*;
import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;
import static net.dillon.qualityofqueso.helper.ModHelper.options;
import static net.dillon.qualityofqueso.keybind.ModKeyMappings.*;

/**
 * Holds management related methods.
 */
public class ManagementInstance implements ModInstance {
    private final QuesoScreen screen;

    public ManagementInstance(QuesoScreen screen) {
        this.screen = screen;
    }

    /**
     * The current searching instance to use.
     * <p>For accessing search fields, use {@link WidgetHandler}.</p>
     */
    public SearchInstance searchInstance() {
        return new SearchInstance(this.screen);
    }

    /**
     * @return the current transferring instance to use.
     */
    public TransferInstance transferInstance() {
        return new TransferInstance(this.screen);
    }

    /**
     * @return the current locked slot instance to use.
     */
    public LockedSlotsInstance lockedSlotsInstance() {
        return new LockedSlotsInstance(this.screen);
    }

    /**
     * @return the current sorting instance to use.
     */
    public SortingInstance sortingInstance() {
        return new SortingInstance(this.screen);
    }

    /**
     * @return the current widget handler instance to use.
     */
    public WidgetHandlerInstance widgetHandlerInstance() {
        return new WidgetHandlerInstance(this.screen);
    }

    /**
     * @return the current quick equip instance to use.
     */
    public QuickEquipInstance quickEquipInstance() {
        return new QuickEquipInstance(this.screen);
    }

    /**
     * @return current screen's cursor stack.
     */
    public ItemStack getCursorStack() {
        return instance().getScreenMenu().getCarried();
    }

    /**
     * @return the current {@code container size.}
     */
    public int getContainerSize() {
        return instance().getCurrentInventory() == null ? 0 : instance().getCurrentInventory().getContainerSize();
    }

    /**
     * @return the total size of the {@code screen's slots.}
     */
    public int getTotalSlots() {
        return instance().getScreenMenu().slots.size();
    }

    /**
     * @return the fromInventory (size) that should be searched.
     */
    public int getInventorySize() {
        return options().accessibility.searchInventory ? instance().getScreenMenu().slots.size() : instance().getCurrentInventory() == null ? 0 : instance().getCurrentInventory().getContainerSize();
    }

    /**
     * @return if the tag is a fabric tag.
     * <p>Automatically returns {@code true} if "ignore fabric tags" is disabled.</p>
     */
    public boolean isFabricTag(String tagLocation) {
        return options().accessibility.ignoreFabricTags && tagLocation.startsWith("c:");
    }

    /**
     * @return if the stack is a bundle.
     */
    public boolean isBundle(ItemStack stack) {
        return !stack.isEmpty() && stack.is(ItemTags.BUNDLES);
    }

    /**
     * @return if the click can quickly move an item.
     */
    public boolean canQuickMove(MouseButtonEvent event) {
        return modEnabled(Minecraft.getInstance())
                && event.button() == 0
                && !isExcludingOrLockingSlots()
                && instance().getManagementButtons().alwaysQuickMove() != null
                && !transferInstance().canSingularMove()
                ? options().isAlwaysQuickMove() || event.hasShiftDown()
                : event.hasShiftDown();
    }

    /**
     * @return {@code true} if a slot is found from the query, indicating if a button should be active or not.
     */
    public boolean shouldButtonBeActive(boolean isPlayerInventory, @Nullable Inventory playerInventory) {
        return shouldButtonBeActive(isPlayerInventory, playerInventory, true, false);
    }

    /**
     * @param isPlayerInventory means button status is directed towards the transfer inventory button
     * @return if the button should be active.
     */
    public boolean shouldButtonBeActive(boolean isPlayerInventory, @Nullable Inventory playerInventory, boolean applyFillWhatsPresentFilter, boolean applyFillStacksFilter) {
        if (instance().getCurrentInventory() == null || (isPlayerInventory && playerInventory == null)) {
            return false;
        }

        boolean toInventory = !isPlayerInventory;
        int size = isPlayerInventory ? playerInventory.getNonEquipmentItems().size() : instance().getCurrentInventory().getContainerSize();
        int filledSlots = 0;

        // Determine the size to iterate through
        if (screen instanceof BrewingStandScreen brewingScreen) {
            size = brewingStand(brewingScreen).getContainerSize() - 2;
        } else if (screen instanceof AbstractFurnaceScreen<?> abstractFurnaceScreen
                && !abstractFurnaceScreen.getMenu().getResultSlot().hasItem()) {
            return false;
        } else if (screen instanceof DispenserScreen dispenserScreen) {
            size = dispenser(dispenserScreen).getContainerSize();
        } else if (screen instanceof HopperScreen hopperScreen) {
            size = hopper(hopperScreen).getContainerSize();
        }

        // Loop through all slots
        for (int i = 0; i < size; i++) {
            Slot slot = instance().getScreenMenu().getSlot(i);
            ItemStack stack = isPlayerInventory ? playerInventory.getItem(i) : slot.getItem();
            ItemStack cursorStack = getCursorStack();
            boolean isShulkerScreen = isShulkerBoxScreen(instance().getScreen());
            boolean isCursorShulker = isShulkerScreen && instance().getScreenMenu().getCarried().is(ItemTags.SHULKER_BOXES);
            boolean isStackShulker = isShulkerScreen && stack.is(ItemTags.SHULKER_BOXES);

            // Skip stacks that are not what the user is attempting to drop
            if (hoveredSlotHasItem(instance().getScreensHoveredSlot()) && hasQuickDropKeysDown() && !stack.is(instance().getScreensHoveredSlot().getItem().getItem())) {
                continue;
            }

            // Skip slots that are not already present
            if (applyFillWhatsPresentFilter && options().management.moveMatchingItems && !isPresent(toInventory, !cursorStack.isEmpty() ? cursorStack : stack)) {
                continue;
            }

            // Skip locked slots
            if (isPlayerInventory) {
                if (lockedSlotsInstance().getLockedPlayerSlots().contains(i)) {
                    continue;
                }
            } else if (lockedSlotsInstance().isLockedSlot(slot.index)) {
                continue;
            }

            // Test if cursor stacks are present
            if (!cursorStack.isEmpty()) {
                boolean canMoveCursorStack = false;
                for (int k = 0; k < getTotalSlots(); k++) {
                    if (isPlayerInventory && instance().getScreenMenu().getSlot(k).index < instance().getCurrentInventory().getContainerSize()) {
                        continue;
                    } else if (!isPlayerInventory && instance().getScreenMenu().getSlot(k).index > instance().getCurrentInventory().getContainerSize()) {
                        continue;
                    }

                    if (canMoveCursorItem(instance().getScreenMenu().getSlot(k), true, isPlayerInventory)) {
                        canMoveCursorStack = true;
                        break;
                    }
                }

                if (canMoveCursorStack && !isCursorShulker) {
                    filledSlots++;
                }
            } else {
                // Test if any stack is present
                if (!stack.isEmpty() && !isStackShulker) {
                    filledSlots++;
                }
            }
        }
        return filledSlots != 0
                && !areAllSlotsUnavailable(applyFillWhatsPresentFilter, isPlayerInventory, toInventory, isPlayerInventory ? playerInventory : null)
                && (!applyFillStacksFilter || !options().isFillStacksEnabled() || hasFillStacksTransferCandidate(isPlayerInventory, playerInventory, toInventory, applyFillWhatsPresentFilter));
    }

    /**
     * @return {@code true} if all slots are grayed out, or {@code unavailable.}
     */
    public boolean areAllSlotsUnavailable(boolean applyFillWhatsPresentFilter, boolean isPlayerInventory, boolean toInventory, @Nullable Inventory playerInventory) {
        int foundQueries = 0;

        if (isPlayerInventory) {
            for (Slot slot : instance().getScreenMenu().slots) {
                if (slot.container != playerInventory) {
                    continue;
                }

                if (isExcludedSlot(slot.index)) {
                    continue;
                }

                if (searchInstance().search(instance().getSearchFields().searchText(), slot, false)) {
                    if (!lockedSlotsInstance().isLockedSlot(slot.index) && !(isShulkerBoxScreen(instance().getScreen()) && slot.getItem().is(ItemTags.SHULKER_BOXES))) {
                        if (!applyFillWhatsPresentFilter || !options().management.moveMatchingItems || isPresent(toInventory, slot.getItem())) {
                            foundQueries++;
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < instance().getCurrentInventory().getContainerSize(); i++) {
                Slot slot = instance().getScreenMenu().getSlot(i);

                if (lockedSlotsInstance().isLockedSlot(slot.index)) {
                    continue;
                }

                if (isExcludedSlot(slot.index)) {
                    continue;
                }

                if (searchInstance().search(instance().getSearchFields().searchText(), slot, false)) {
                    foundQueries++;
                }
            }
        }

        return foundQueries == 0;
    }

    /**
     * @return if the user is attempting to exclude or lock slots.
     */
    public boolean isExcludingOrLockingSlots() {
        return isValidScreen(instance().getScreen())
                && (hasSelectSlotsKeyDown() || hasExcludeSlotsKeyDown())
                && getHoveredSlot(instance().getScreen()) != null
                && instance().getScreenMenu().getCarried().isEmpty();
    }

    /**
     * @return {@code true} if the slot is valid to move.
     */
    public boolean isHotbarSlot(int totalSlots, int slotIndex) {
        // If we're in the last 9 slots and hotbar is disabled, return false
        return slotIndex >= totalSlots - 9;
    }

    /**
     * @return {@code true} if the slot is a hotbar slot in the fromInventory.
     */
    public boolean isInventoryHotbarSlot(boolean isInventoryScreen, int slotIndex) {
        // Index 36-44 are hotbar slots in INVENTORY screen.
        return isInventoryScreen && slotIndex <= 44 && slotIndex >= 36;
    }

    /**
     * @return {@code true} if the slot should be excluded entirely.
     */
    public boolean isExcludedInventorySlot(int slotIndex) {
        // Index 5-8 are armor slots. Index 45 is offhand slot. NEVER drop those items.
        return isInventoryScreen(instance().getScreen()) && (slotIndex <= 8 && slotIndex >= 5 || slotIndex == 45);
    }

    /**
     * @return {@code true} if any slot (with the range provided) is filled.
     * <p>{@code default start = 9, default end = 36}</p>
     */
    public boolean isAnySlotFilled(boolean checkHotbar, int start, int end) {
        for (int i = start; i < (options().management.includeHotbar && checkHotbar ? end + 9 : end); i++) {
            Slot slot = instance().getScreenMenu().getSlot(i);
            if (slot.hasItem()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return if a slot should be skipped.
     */
    public boolean isExcludedSlot(int slotId) {
        for (int id : instance().getExcludedSlots()) {
            if (slotId == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return if a stack can be combined/merged.
     */
    public boolean canCombine(ItemStack a, ItemStack b) {
        if (a.isEmpty() || b.isEmpty()) {
            return false;
        }
        if (!a.isStackable()) {
            return false;
        }
        if (!ItemStack.isSameItem(a, b)) {
            return false;
        }
        return ItemStack.isSameItemSameComponents(a, b);
    }

    /**
     * Handles smart-moving actions.
     */
    public boolean canMoveCursorItem(Slot fromSlot, boolean ignoreComponents, boolean isPlayerInventory) {
        // Cannot move excluded hotbar slot
        if (!options().management.includeHotbar && isPlayerInventory && isHotbarSlot(getTotalSlots(), fromSlot.index)) {
            return false;
        }

        ItemStack fromStack = fromSlot.getItem();
        if (Minecraft.getInstance().hasShiftDown() && !ignoreComponents) {
            return ItemStack.isSameItemSameComponents(fromStack.copy(), getCursorStack());
        }
        return ItemStack.isSameItem(fromStack.copy(), getCursorStack());
    }

    /**
     * @return if a stack is in a tag.
     */
    public boolean areStacksInSameTag(ItemStack fromStack, ItemStack toStack) {
        return fromStack.tags().anyMatch(tag -> !isFabricTag(tag.location().toString()) && toStack.is(tag));
    }

    /**
     * @return if two stacks match under the current fill filter mode.
     */
    public boolean matchesFillFilter(ItemStack fromStack, ItemStack toStack) {
        boolean areMatching = fromStack.getItem() == toStack.getItem();
        if (ContainerHelper.IS_TRACKED_CONTAINER && ContainerHelper.CURRENT_FILTER_MODE.tag()) {
            return areStacksInSameTag(fromStack, toStack) || areMatching;
        }
        return areMatching;
    }

    /**
     * @return true if the item is present in the opposing inventory/container.
     */
    public boolean isPresent(boolean toInventory, ItemStack sourceStack) {
        if (!isContainerScreen(instance().getScreen()) && !isShulkerBoxScreen(instance().getScreen())) {
            return true;
        }

        if (ContainerHelper.IS_TRACKED_CONTAINER) {
            return toInventory || itemMatchesPlaceholder(sourceStack);
        }

        int containerSize = getContainerSize();
        int slotSize = getTotalSlots();
        for (int i = (toInventory ? containerSize : 0); i < (toInventory ? slotSize : containerSize); i++) {
            ItemStack opposingStack = instance().getScreenMenu().getSlot(i).getItem();
            if (!opposingStack.isEmpty() && matchesFillFilter(sourceStack, opposingStack)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return true if the stack matches any placeholder in the current tracked container.
     */
    public boolean itemMatchesPlaceholder(ItemStack sourceStack) {
        if (!ContainerHelper.IS_TRACKED_CONTAINER) {
            return false;
        }

        for (ItemStack placeholderStack : ContainerHelper.getCurrentPlaceholderStacks()) {
            if (!placeholderStack.isEmpty() && matchesFillFilter(sourceStack, placeholderStack)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return if stacks match, and handles bundling.
     */
    public boolean areEquivalentSortStacks(ItemStack a, ItemStack b, boolean strictCountMatch) {
        if (a.isEmpty() || b.isEmpty()) {
            return a.isEmpty() && b.isEmpty();
        }
        boolean sameItem = isBundle(a) || isBundle(b)
                ? ItemStack.isSameItemSameComponents(a, b)
                : ItemStack.isSameItem(a, b);
        return sameItem && (!strictCountMatch || a.getCount() == b.getCount());
    }

    /**
     * Performs the {@code click action} to sort items.
     */
    public void clickSlot(int slotId) {
        instance().getMinecraft().gameMode.handleContainerInput(
                instance().getScreenMenu().containerId,
                slotId,
                0,
                ContainerInput.PICKUP,
                instance().getMinecraft().player
        );
    }

    /**
     * Sends a "swap slot" click packet.
     */
    public void sendSwapSlotPacket(int source, int index) {
        sendClickSlotPacket(source, ContainerInput.PICKUP);
        sendClickSlotPacket(index, ContainerInput.PICKUP);
        sendClickSlotPacket(source, ContainerInput.PICKUP);
    }

    /**
     * Sends a click slot packet.
     */
    public void sendClickSlotPacket(int slotIndex, ContainerInput containerInput) {
        Minecraft client = Minecraft.getInstance();
        ClientPacketListener packetListener = client.getConnection();

        if (client.player == null || packetListener == null || client.player.containerMenu == null) {
            return;
        }

        var handler = client.player.containerMenu;
        int syncId = handler.containerId;
        int stateId = handler.getStateId();

        if (containerInput == ContainerInput.THROW) {
            ItemStack clickedStack = handler.getSlot(slotIndex).getItem();
            if (!clickedStack.isEmpty()) {
                client.gameMode.handleContainerInput(syncId, slotIndex, hasDropOnlyOneItemKeyDown() ? 0 : 1, containerInput, client.player);
            }
            return;
        }

        ItemStack carriedStack = handler.getCarried();
        ItemStack clickedStack = handler.getSlot(slotIndex).getItem();

        HashedPatchMap.HashGenerator hasher = packetListener.decoratedHashOpsGenenerator();

        HashedStack carriedHash = HashedStack.create(carriedStack, hasher);
        HashedStack clickedHash = HashedStack.create(clickedStack, hasher);

        Int2ObjectOpenHashMap<HashedStack> modifiedStacks = new Int2ObjectOpenHashMap<>();
        modifiedStacks.put(slotIndex, clickedHash);

        ServerboundContainerClickPacket packet = new ServerboundContainerClickPacket(
                syncId,
                stateId,
                (short) slotIndex,
                (byte) 0,
                containerInput,
                modifiedStacks,
                carriedHash
        );

        packetListener.send(packet);
    }

    /**
     * @return {@code true} if at least one source stack can fill an existing matching destination stack.
     */
    public boolean hasFillStacksTransferCandidate(boolean isPlayerInventory, @Nullable Inventory playerInventory, boolean toInventory, boolean applyFillWhatsPresentFilter) {
        ItemStack cursorStack = getCursorStack();
        int containerSize = getContainerSize();
        int totalSlots = getTotalSlots();
        int toStart = toInventory ? containerSize : 0;
        int toEnd = toInventory ? totalSlots : containerSize;

        if (!isContainerScreen(instance().getScreen()) && !isShulkerBoxScreen(instance().getScreen())) {
            return true;
        }

        for (Slot sourceSlot : instance().getScreenMenu().slots) {
            if (isPlayerInventory && sourceSlot.container != playerInventory) {
                continue;
            }
            if (!isPlayerInventory && sourceSlot.index >= containerSize) {
                continue;
            }
            if (sourceSlot.getItem().isEmpty()) {
                continue;
            }
            if (isExcludedSlot(sourceSlot.index)) {
                continue;
            }
            if (!searchInstance().search(instance().getSearchFields().searchText(), sourceSlot, false)) {
                continue;
            }
            if (lockedSlotsInstance().isLockedSlot(sourceSlot.index)) {
                continue;
            }
            if (isShulkerBoxScreen(instance().getScreen()) && sourceSlot.getItem().is(ItemTags.SHULKER_BOXES)) {
                continue;
            }
            if (applyFillWhatsPresentFilter
                    && options().management.moveMatchingItems
                    && !isPresent(toInventory, !cursorStack.isEmpty() ? cursorStack : sourceSlot.getItem())) {
                continue;
            }
            if (!cursorStack.isEmpty() && !canMoveCursorItem(sourceSlot, false, isPlayerInventory)) {
                continue;
            }
            if (getDestinationFreeSpaceForStack(sourceSlot.getItem(), toStart, toEnd) > 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return total free space in matching destination stacks in the target range.
     */
    public int getDestinationFreeSpaceForStack(ItemStack sourceStack, int toStart, int toEnd) {
        int totalFreeSpace = 0;
        for (int j = toStart; j < toEnd; j++) {
            Slot toSlot = instance().getScreenMenu().getSlot(j);
            ItemStack toStack = toSlot.getItem();
            if (toStack.isEmpty()) {
                continue;
            }
            if (!ItemStack.isSameItemSameComponents(sourceStack, toStack)) {
                continue;
            }
            int maxStackSize = Math.min(toStack.getMaxStackSize(), toSlot.getMaxStackSize(toStack));
            int freeSpace = maxStackSize - toStack.getCount();
            if (freeSpace > 0) {
                totalFreeSpace += freeSpace;
            }
        }
        return totalFreeSpace;
    }

    /**
     * @return {@code true} if the container (or inventory) is full, including/excluding hotbar slots.
     */
    public boolean isContainerFull(boolean inventory) {
        AbstractContainerMenu menu = instance().getScreenMenu();
        var options = options();

        int containerSize = getContainerSize();

        int inventorySize = options.management.includeHotbar ? 36 : 27;
        int inventoryEnd = containerSize + inventorySize;

        int fromStart = inventory ? containerSize : 0;
        int fromEnd = inventory ? inventoryEnd : containerSize;

        int toStart = inventory ? 0 : containerSize;
        int toEnd = inventory ? containerSize : inventoryEnd;

        int checkStart = inventory ? containerSize : 0;
        int checkEnd = inventory ? inventoryEnd : containerSize;

        int filledSlots = 0;
        Map<Object, Integer> componentFreeSpace = new HashMap<>();
        for (int i = checkStart; i < checkEnd; i++) {
            ItemStack stack = menu.getSlot(i).getItem();
            if (stack.isEmpty()) {
                continue;
            }

            int free = stack.getMaxStackSize() - stack.getCount();
            if (free > 0) {
                componentFreeSpace.merge(stack.getComponents(), free, Math::max);
            }
        }

        for (int i = fromStart; i < fromEnd; i++) {
            Slot fromSlot = menu.getSlot(i);
            ItemStack fromItem = fromSlot.getItem();

            if (fromSlot.hasItem()) {
                filledSlots++;
            }

            if (fromItem.isEmpty()) {
                continue;
            }

            for (int k = toStart; k < toEnd; k++) {
                ItemStack toItem = menu.getSlot(k).getItem();
                if (toItem.isEmpty()) {
                    continue;
                }

                if (ItemStack.isSameItem(toItem, fromItem)) {
                    Integer free = componentFreeSpace.get(toItem.getComponents());
                    if (free != null && free > 0) {
                        return false;
                    }
                }
            }
        }

        return filledSlots == (inventory ? inventorySize : containerSize);
    }

    /**
     * @return the temporary slots (specifically for bundles).
     */
    public int findTemporaryEmptySlotForBundle(int a, int b, List<Integer> allowedSlots) {
        for (int i : allowedSlots) {
            if (i == a || i == b) {
                continue;
            }

            if (!instance().getScreenMenu().slots.get(i).hasItem()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @return a temporary empty slot for parking the carried stack.
     */
    public int findTemporaryEmptySlotForCursor() {
        for (Slot slot : instance().getScreenMenu().slots) {
            if (slot.hasItem()) {
                continue;
            }
            if (isExcludedSlot(slot.index)) {
                continue;
            }
            if (options().lockedSlots.enableLockedSlots && lockedSlotsInstance().isLockedSlot(slot.index)) {
                continue;
            }
            return slot.index;
        }
        return -1;
    }

    @Override
    public QuesoScreen instance() {
        return this.screen;
    }
}