package net.dillon.qualityofqueso.instance.management;

import net.dillon.qualityofqueso.instance.QuesoScreen;
import net.dillon.qualityofqueso.option.eum.management.sorting.CurrentSortingMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

import static net.dillon.dillonlib.task.ClientTasks.getScreen;
import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Handles sorting features.
 */
public class SortingInstance extends ManagementInstance {

    public SortingInstance(QuesoScreen screen) {
        super(screen);
    }

    /**
     * @return if a container can be sorted.
     */
    public boolean canSort() {
        boolean inventory = isInventoryScreen(instance().getScreen());
        int sortStart = inventory ? 9 : 0;
        int sortEnd = inventory
                ? (client().management().includingHotbar ? 45 : 36)
                : getSortableContainerSize();

        boolean hasSortableItem = false;
        for (int i = sortStart; i < sortEnd; i++) {
            Slot slot = instance().getScreenMenu().getSlot(i);
            if (slot.hasItem()
                    && !isExcludedSlot(slot.index)
                    && !lockedSlotsInstance().isLockedSlot(slot.index)) {
                hasSortableItem = true;
                break;
            }
        }

        return hasSortableItem
                && instance().getSearchFields().searchText().isEmpty()
                && getCursorStack().isEmpty();
    }

    /**
     * Attempts to sort items.
     */
    public void trySort() {
        if (canSort()) {
            sortItems();
            playButtonSound(instance().getMinecraft());
        } else {
            playButtonInactiveSound(instance().getMinecraft());
        }
    }

    /**
     * @return lookup index for item order in the creative mode search tab.
     */
    private Map<Item, Integer> getCreativeSearchTabOrder() {
        Map<Item, Integer> order = new HashMap<>();
        int index = 0;

        // Mirror CreativeModeInventoryScreen initialization so search-tab order
        // exists even if creative inventory was never opened in this session.
        Minecraft client = Minecraft.getInstance();
        ClientPacketListener connection = client.getConnection();
        if (connection != null) {
            var lookupProvider = client.level != null ? client.level.registryAccess() : connection.registryAccess();
            boolean hasOpPerms = client.player != null && client.player.canUseGameMasterBlocks();

            if (CreativeModeTabs.tryRebuildTabContents(connection.enabledFeatures(), hasOpPerms, lookupProvider)) {
                List<ItemStack> searchItems = List.copyOf(CreativeModeTabs.searchTab().getDisplayItems());
                connection.searchTrees().updateCreativeTooltips(lookupProvider, searchItems);
                connection.searchTrees().updateCreativeTags(searchItems);
            }
        }

        for (ItemStack stack : CreativeModeTabs.searchTab().getDisplayItems()) {
            if (stack.isEmpty()) {
                continue;
            }

            // Keep the first appearance so duplicates from variants do not reorder the base item.
            if (order.putIfAbsent(stack.getItem(), index) == null) {
                index++;
            }
        }

        return order;
    }

    /**
     * Sorts all items in a container.
     */
    public void sortItems() {
        if (instance().getMinecraft().player == null || instance().getMinecraft().getConnection() == null) {
            return;
        }
        if (!(getScreen() instanceof AbstractContainerScreen<?> || getScreen() instanceof InventoryScreen)) {
            return;
        }

        boolean inventoryScreen = isInventoryScreen(instance().getScreen());
        List<Integer> sortableSlots = new ArrayList<>();

        if (inventoryScreen) {
            Set<Integer> lockedPlayerSlots = lockedSlotsInstance().getLockedPlayerSlots();
            int end = client().management().includingHotbar ? 45 : 36;
            for (int slotIndex = 9; slotIndex < end; slotIndex++) {
                int playerSlotId = slotIndex >= 36 ? slotIndex - 36 : slotIndex;
                if (!lockedPlayerSlots.contains(playerSlotId) && !(isExcludedSlot(slotIndex) && instance().getScreenMenu().getSlot(slotIndex).hasItem())) {
                    sortableSlots.add(slotIndex);
                }
            }
        } else {
            int containerSize = getSortableContainerSize();
            if (containerSize <= 0) {
                return;
            }

            Set<Integer> lockedContainerSlots = lockedSlotsInstance().getLockedContainerSlots();
            for (int i = 0; i < containerSize; i++) {
                if (!lockedContainerSlots.contains(i) && !(isExcludedSlot(i) && instance().getScreenMenu().getSlot(i).hasItem())) {
                    sortableSlots.add(i);
                }
            }
        }

        if (sortableSlots.isEmpty()) {
            return;
        }

        // Merge all stacks. Keep sweeping until a full pass makes no changes.
        boolean mergedAnyInPass;
        do {
            mergedAnyInPass = false;
            for (int sourcePos = 0; sourcePos < sortableSlots.size(); sourcePos++) {
                int i = sortableSlots.get(sourcePos);
                Slot source = instance().getScreenMenu().slots.get(i);
                if (!source.hasItem()) {
                    continue;
                }

                ItemStack sourceStack = source.getItem();

                // Already full, skip
                int sourceMax = Math.min(sourceStack.getMaxStackSize(), source.getMaxStackSize(sourceStack));
                if (sourceStack.getCount() >= sourceMax) {
                    continue;
                }

                for (int targetPos = sourcePos + 1; targetPos < sortableSlots.size(); targetPos++) {
                    int j = sortableSlots.get(targetPos);
                    Slot target = instance().getScreenMenu().slots.get(j);
                    if (!target.hasItem()) {
                        continue;
                    }

                    ItemStack targetStack = target.getItem();

                    if (!canCombine(sourceStack, targetStack)) {
                        continue;
                    }

                    int sourceBefore = source.getItem().getCount();
                    int targetBefore = target.getItem().getCount();

                    // Pick up target
                    clickSlot(j);
                    // Click source to merge
                    clickSlot(i);

                    // If cursor still has items, put them back
                    if (!instance().getMinecraft().player.containerMenu.getCarried().isEmpty()) {
                        clickSlot(j);
                    }

                    if (source.getItem().getCount() != sourceBefore || target.getItem().getCount() != targetBefore) {
                        mergedAnyInPass = true;
                    }

                    // Stop if source is now full
                    ItemStack sourceAfter = source.getItem();
                    int sourceAfterMax = Math.min(sourceAfter.getMaxStackSize(), source.getMaxStackSize(sourceAfter));
                    if (sourceAfter.getCount() >= sourceAfterMax) {
                        break;
                    }
                }
            }
        } while (mergedAnyInPass);

        // Build list from LIVE slots AFTER merge
        List<ItemStack> stacks = new ArrayList<>();
        for (int slotIndex : sortableSlots) {
            ItemStack stack = instance().getScreenMenu().slots.get(slotIndex).getItem();
            if (!stack.isEmpty()) {
                stacks.add(stack.copy());
            }
        }
        stacks = normalizeStacks(stacks);

        // Unified alphabetical sort (no stackability logic)
        Map<ItemStack, String> tagCache = new HashMap<>();

        for (ItemStack stack : stacks) {
            String tagKey = "";

            if (client().sorting().currentSortingMode.tag()) {
                tagKey = stack.tags()
                        .map(tag -> {
                            String location = tag.location().toString();
                            return !isFabricTag(location) ? location : "";
                        })
                        .sorted()
                        .findFirst()
                        .orElse("");
            }

            tagCache.put(stack, tagKey);
        }

        boolean countSort = client().sorting().currentSortingMode.count();
        boolean creativeTabSort = client().sorting().currentSortingMode == CurrentSortingMode.CREATIVE_MENU;
        Map<Item, Integer> creativeOrder = creativeTabSort ? getCreativeSearchTabOrder() : Collections.emptyMap();
        Comparator<ItemStack> alphaComparator = Comparator.comparing((ItemStack stack) -> tagCache.get(stack).isEmpty())
                .thenComparing(tagCache::get)
                .thenComparing(stack ->
                        stack.getCustomName() != null
                                ? stack.getCustomName().getString()
                                : stack.getItemName().getString()
                )
                .thenComparing(Comparator.comparingInt(ItemStack::getCount).reversed());

        if (creativeTabSort) {
            stacks.sort(Comparator
                    .comparingInt((ItemStack stack) -> creativeOrder.getOrDefault(stack.getItem(), Integer.MAX_VALUE))
                    .thenComparing(alphaComparator));
        } else if (countSort) {
            Comparator<ItemStack> comp = client().sorting().currentSortingMode == CurrentSortingMode.COUNT_DESCENDING
                    ? Comparator.comparingInt(ItemStack::getCount).reversed()
                    : Comparator.comparingInt(ItemStack::getCount);
            stacks.sort(comp.thenComparing(alphaComparator));
        } else {
            stacks.sort(alphaComparator);
        }

        // Pad with empties
        while (stacks.size() < sortableSlots.size()) {
            stacks.add(ItemStack.EMPTY);
        }

        // Perform swap-based sorting
        // Keep count as part of identity so full stacks are placed before partial stacks.
        boolean strictCountMatch = true;
        for (int targetPos = 0; targetPos < sortableSlots.size(); targetPos++) {
            int target = sortableSlots.get(targetPos);
            ItemStack desired = stacks.get(targetPos);
            ItemStack actual = instance().getScreenMenu().slots.get(target).getItem();

            if (areEquivalentSortStacks(actual, desired, strictCountMatch)) {
                continue;
            }

            int source = findMatchingSlot(desired, sortableSlots, targetPos, strictCountMatch);
            if (source == -1) {
                continue;
            }

            transferInstance().swapSlots(source, target, sortableSlots);
            clearCarriedStack(sortableSlots);
        }

        clearCarriedStack(sortableSlots);
    }

    /**
     * @return the container slot count that should be sortable on the current screen.
     * Falls back for modded container screens that do not expose a direct container instance.
     */
    private int getSortableContainerSize() {
        int directSize = getContainerSize();
        if (directSize > 0) {
            return directSize;
        }

        int totalSlots = getTotalSlots();
        if (totalSlots <= 0) {
            return 0;
        }

        // Prefer locating where the player's inventory section actually begins.
        // This avoids assuming a fixed 36-slot tail on modded menus that expose extra player slots.
        if (instance().getMinecraft().player != null) {
            AbstractContainerMenu menu = instance().getScreenMenu();
            Inventory playerInventory = instance().getMinecraft().player.getInventory();
            for (int i = 0; i < totalSlots; i++) {
                if (menu.getSlot(i).container == playerInventory) {
                    return i;
                }
            }
        }

        // Fallback for menus that still follow vanilla-style trailing player inventory layout.
        if (totalSlots > 36) {
            return totalSlots - 36;
        }

        return totalSlots;
    }

    /**
     * Fully merges identical stacks by item+components in-memory, then expands
     * them into max-sized stacks so sorting is deterministic.
     */
    private List<ItemStack> normalizeStacks(List<ItemStack> input) {
        class StackGroup {
            final ItemStack sample;
            int total;

            StackGroup(ItemStack sample, int total) {
                this.sample = sample;
                this.total = total;
            }
        }

        List<StackGroup> groups = new ArrayList<>();
        for (ItemStack stack : input) {
            if (stack.isEmpty()) {
                continue;
            }

            StackGroup found = null;
            for (StackGroup group : groups) {
                if (ItemStack.isSameItemSameComponents(group.sample, stack)) {
                    found = group;
                    break;
                }
            }

            if (found == null) {
                groups.add(new StackGroup(stack.copy(), stack.getCount()));
            } else {
                found.total += stack.getCount();
            }
        }

        List<ItemStack> normalized = new ArrayList<>();
        for (StackGroup group : groups) {
            int max = Math.max(1, group.sample.getMaxStackSize());
            int remaining = group.total;

            while (remaining > 0) {
                int take = Math.min(max, remaining);
                normalized.add(group.sample.copyWithCount(take));
                remaining -= take;
            }
        }

        return normalized;
    }

    /**
     * Tries to never leave a stack on the cursor during/after sort.
     */
    private void clearCarriedStack(List<Integer> allowedSlots) {
        AbstractContainerMenu menu = instance().getScreenMenu();
        ItemStack carried = menu.getCarried();
        if (carried.isEmpty()) {
            return;
        }

        // Prefer merging into matching stacks first.
        for (int i : allowedSlots) {
            ItemStack slotStack = menu.slots.get(i).getItem();
            if (!canCombine(carried, slotStack)) {
                continue;
            }

            int max = Math.min(slotStack.getMaxStackSize(), menu.slots.get(i).getMaxStackSize(slotStack));
            if (slotStack.getCount() >= max) {
                continue;
            }

            clickSlot(i);
            if (menu.getCarried().isEmpty()) {
                return;
            }
            carried = menu.getCarried();
        }

        // Fallback: place in any empty slot.
        for (int i : allowedSlots) {
            if (!menu.slots.get(i).hasItem()) {
                clickSlot(i);
                if (menu.getCarried().isEmpty()) {
                    return;
                }
            }
        }
    }

    /**
     * Finds a slot matching the target merging slot.
     *
     * @return the slot index.
     */
    private int findMatchingSlot(ItemStack target, List<Integer> sortableSlots, int startPos, boolean strictCountMatch) {
        if (target.isEmpty()) {
            return -1;
        }

        for (int i = startPos + 1; i < sortableSlots.size(); i++) {
            int slotIndex = sortableSlots.get(i);
            ItemStack stack = instance().getScreenMenu().slots.get(slotIndex).getItem();
            if (areEquivalentSortStacks(stack, target, strictCountMatch)) {
                return slotIndex;
            }
        }
        return -1;
    }
}
