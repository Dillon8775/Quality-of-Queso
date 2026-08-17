package net.dillon.qualityofqueso.helper;

import net.blay09.mods.balm.Balm;
import net.dillon.qualityofqueso.option.ContainerData;
import net.dillon.qualityofqueso.option.LockedContainerSlots;
import net.dillon.qualityofqueso.option.LockedPlayerSlots;
import net.dillon.qualityofqueso.option.eum.management.FilteringMode;
import net.dillon.qualityofqueso.option.eum.management.sorting.CurrentSortingMode;
import net.dillon.qualityofqueso.option.eum.management.sorting.DefaultSortingMode;
import net.dillon.qualityofqueso.packet.RequestShulkerStateC2SPacket;
import net.dillon.qualityofqueso.packet.SyncShulkerStateS2CPacket;
import net.dillon.qualityofqueso.packet.UpdateShulkerStateC2SPacket;
import net.dillon.qualityofqueso.util.ShulkerStateHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.storage.LevelResource;

import java.util.*;
import java.util.stream.Collectors;

import static net.dillon.qualityofqueso.helper.ManagementHelper.playLockSlotSound;
import static net.dillon.qualityofqueso.option.OptionInstances.*;

/**
 * Tracks client-selected containers.
 */
public class ContainerHelper {
    public enum FilteringType {
        ITEM,
        TAG;

        /**
         * @return {@code true} when this mode is tag-based.
         */
        public boolean tag() {
            return this == TAG;
        }
    }

    public static boolean IS_TRACKED_CONTAINER = false;
    public static boolean OPENING_PLACEHOLDER_SCREEN = false;
    public static boolean RETURNING_FROM_PLACEHOLDER_SCREEN = false;
    public static FilteringType CURRENT_FILTER_TYPE = FilteringType.ITEM;
    public static FilteringMode CURRENT_FILTER_MODE = FilteringMode.MATCHING;
    private static boolean capturedGlobalSortingModeForActiveContainer = false;
    private static Set<String> pendingOpenedContainerKeys = null;
    private static Set<String> activeContainerKeys = null;
    private static BlockPos pendingOpenedShulkerPos = null;
    private static BlockPos activeShulkerPos = null;
    private static final Map<BlockPos, ShulkerState> shulkerStateCache = new HashMap<>();

    /**
     * @return {@code true} when tracked-container filtering behavior should be active.
     */
    public static boolean isTrackedFilteringActive() {
        return IS_TRACKED_CONTAINER && client().management().containerFiltering;
    }

    /**
     * @return valid block entities for tracking.
     */
    public static boolean isValidBlockEntity(BlockEntity blockEntity) {
        return blockEntity instanceof ChestBlockEntity || blockEntity instanceof EnderChestBlockEntity || blockEntity instanceof ShulkerBoxBlockEntity || blockEntity instanceof BarrelBlockEntity;
    }

    /**
     * Clears active container state when closing a container screen.
     */
    public static void clearActiveContainer() {
        activeContainerKeys = null;
        activeShulkerPos = null;
        CURRENT_FILTER_TYPE = FilteringType.ITEM;
        CURRENT_FILTER_MODE = FilteringMode.MATCHING;
        capturedGlobalSortingModeForActiveContainer = false;
    }

    /**
     * Clears pending interaction state when no chest interaction occurred.
     */
    public static void clearPendingOpened() {
        pendingOpenedContainerKeys = null;
        pendingOpenedShulkerPos = null;
    }

    /**
     * @return the tracked chests list.
     */
    private static Set<String> itemFilteredContainers() {
        if (containerData().itemFilteredContainers == null) {
            containerData().itemFilteredContainers = new HashSet<>();
        }
        return containerData().itemFilteredContainers;
    }

    /**
     * @return the tracked tag-filtered container list.
     */
    private static Set<String> tagFilteredContainers() {
        if (containerData().tagFilteredContainers == null) {
            containerData().tagFilteredContainers = new HashSet<>();
        }
        return containerData().tagFilteredContainers;
    }

    /**
     * @return the tracked placeholder item IDs by container key group.
     */
    private static Map<String, List<String>> containerFilterItems() {
        if (containerData().containerFilterItems == null) {
            containerData().containerFilterItems = new HashMap<>();
        }
        return containerData().containerFilterItems;
    }

    /**
     * @return the tracked sorting modes by container key group.
     */
    private static Map<String, String> containerSortingModes() {
        if (containerData().containerSortingModes == null) {
            containerData().containerSortingModes = new HashMap<>();
        }
        return containerData().containerSortingModes;
    }

    /**
     * @return the tracked filtering modes by container key group.
     */
    private static Map<String, String> containerFilteringModes() {
        if (containerData().containerFilteringModes == null) {
            containerData().containerFilteringModes = new HashMap<>();
        }
        return containerData().containerFilteringModes;
    }

    /**
     * @return the locked player slot map.
     */
    private static Map<String, List<Integer>> playerLockedSlotsMap() {
        if (lockedPlayerSlots().lockedPlayerSlots == null) {
            lockedPlayerSlots().lockedPlayerSlots = new HashMap<>();
        }
        return lockedPlayerSlots().lockedPlayerSlots;
    }

    /**
     * @return the locked container slot map.
     */
    private static Map<String, List<Integer>> containerLockedSlotsMap() {
        if (lockedContainerSlots().lockedContainerSlots == null) {
            lockedContainerSlots().lockedContainerSlots = new HashMap<>();
        }
        return lockedContainerSlots().lockedContainerSlots;
    }

    /**
     * @return current active container key group used for placeholder persistence.
     */
    private static String activeContainerGroupKey() {
        return containerGroupKey(activeContainerKeys);
    }

    /**
     * @return stable key for a group of container keys.
     */
    private static String containerGroupKey(Set<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return "";
        }

        List<String> sorted = new ArrayList<>(keys);
        sorted.sort(Comparator.naturalOrder());
        return String.join(";", sorted);
    }

    /**
     * @return stable key used for persisted sort-mode state of the active context.
     */
    private static String activeSortKey() {
        if (activeShulkerPos != null) {
            return shulkerSortKey(activeShulkerPos);
        }
        return activeContainerGroupKey();
    }

    /**
     * @return stable key used for persisted sort-mode state of a shulker position.
     */
    private static String shulkerSortKey(BlockPos pos) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return "";
        }
        return worldKey() + " / " + dimensionKey(minecraft.level) + " / minecraft:shulker_box ("
                + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")";
    }

    /**
     * @return if the box has an active sorting mode.
     */
    private static boolean hasActiveSortContext() {
        return activeShulkerPos != null || !activeSortKey().isEmpty();
    }

    /**
     * Applies saved sort mode for the active context.
     * If no mode is stored, resolves from current default and persists it.
     */
    private static void applyActiveTrackedSortMode() {
        if (!hasActiveSortContext()) {
            return;
        }

        // Do not set sorting mode based on container if user is using a global sorting mode.
        if (client().sorting().useGlobalSortingMode) {
            return;
        }

        // Capture the pre-container sort mode once so close-screen restore is stable.
        if (!capturedGlobalSortingModeForActiveContainer) {
            ModConstants.GLOBAL_SORTING_MODE = client().sorting().currentSortingMode;
            capturedGlobalSortingModeForActiveContainer = true;
        }

        // Get the active shulker's sorting mode, and set the sorting mode to it
        if (activeShulkerPos != null) {
            ShulkerState state = shulkerStateCache.get(activeShulkerPos);
            String sortingMode = state == null ? null : state.sortingMode;
            if (sortingMode == null || sortingMode.isBlank()) {
                CurrentSortingMode mode = getDefaultSortingModeForContainer();
                client().sorting().currentSortingMode = mode;
                if (state != null) {
                    state.sortingMode = mode.name();
                    pushActiveShulkerStateToServer();
                }
            } else {
                client().sorting().currentSortingMode = CurrentSortingMode.fromName(sortingMode);
            }
            return;
        }

        // Otherwise, get a container's sorting mode, and set the sorting mode to it
        String key = activeSortKey();
        if (key.isEmpty()) {
            client().sorting().currentSortingMode = getDefaultSortingModeForContainer();
            return;
        }

        // If the container or object does not have a sorting mode, automatically set it to alphabetical
        String stored = containerSortingModes().get(key);
        if (stored == null || stored.isBlank()) {
            CurrentSortingMode mode = getDefaultSortingModeForContainer();
            client().sorting().currentSortingMode = mode;
            containerSortingModes().put(key, mode.name());
            ContainerData.INSTANCE.save();
            return;
        }

        // Set the sorting mode to the sorting mode's name
        client().sorting().currentSortingMode = CurrentSortingMode.fromName(stored);
    }

    /**
     * @return the default sorting mode to use for unsorted containers.
     */
    private static CurrentSortingMode getDefaultSortingModeForContainer() {
        if (client().sorting().useGlobalSortingMode) {
            return CurrentSortingMode.ALPHABETICAL;
        } else {
            if (client().sorting().defaultSortingMode == DefaultSortingMode.BY_TAG) {
                return CurrentSortingMode.TAG;
            } else if (client().sorting().defaultSortingMode == DefaultSortingMode.ASCENDING) {
                return CurrentSortingMode.COUNT_ASCENDING;
            } else if (client().sorting().defaultSortingMode == DefaultSortingMode.DESCENDING) {
                return CurrentSortingMode.COUNT_DESCENDING;
            } else if (client().sorting().defaultSortingMode == DefaultSortingMode.CREATIVE_MENU) {
                return CurrentSortingMode.CREATIVE_MENU;
            }
        }

        return CurrentSortingMode.ALPHABETICAL;
    }

    /**
     * Persists current sort mode for active context.
     */
    public static void storeActiveSortMode(CurrentSortingMode mode) {
        if (mode == null || !hasActiveSortContext()) {
            return;
        }

        // Stores active shulker state data
        if (activeShulkerPos != null) {
            ShulkerState state = shulkerState(activeShulkerPos);
            state.sortingMode = mode.name();
            pushActiveShulkerStateToServer();
            return;
        }

        // Return out of the active sort key is empty
        String key = activeSortKey();
        if (key.isEmpty()) {
            return;
        }

        // Add the container's sorting mode, and save
        containerSortingModes().put(key, mode.name());
        ContainerData.INSTANCE.save();
    }

    /**
     * Applies saved filtering mode for the active tracked context.
     */
    private static void applyActiveTrackedFilteringMode() {
        if (!IS_TRACKED_CONTAINER) {
            return;
        }
        String key = activeSortKey();
        if (key.isEmpty()) {
            CURRENT_FILTER_MODE = getDefaultFilteringModeForContainer();
            return;
        }

        String stored = containerFilteringModes().get(key);
        if (stored == null || stored.isBlank()) {
            FilteringMode mode = getDefaultFilteringModeForContainer();
            CURRENT_FILTER_MODE = mode;
            containerFilteringModes().put(key, mode.name());
            ContainerData.INSTANCE.save();
            return;
        }

        CURRENT_FILTER_MODE = FilteringMode.valueOf(stored);
    }

    /**
     * @return the default filtering mode to use for unconfigured containers.
     */
    private static FilteringMode getDefaultFilteringModeForContainer() {
        return client().isFillingCurrentStacks() ? FilteringMode.CURRENT_STACKS : FilteringMode.MATCHING;
    }

    /**
     * Persists current filtering mode for the active tracked context.
     */
    private static void storeActiveFilteringMode(FilteringMode mode) {
        if (mode == null || !IS_TRACKED_CONTAINER) {
            return;
        }
        String key = activeSortKey();
        if (key.isEmpty()) {
            return;
        }
        containerFilteringModes().put(key, mode.name());
        ContainerData.INSTANCE.save();
    }

    /**
     * Removes persisted sort mode for a container key group.
     */
    private static void clearStoredSortModeForContainer(Set<String> keys) {
        String groupKey = containerGroupKey(keys);
        if (groupKey.isEmpty()) {
            return;
        }

        // Remove container sorting mode if it's not null
        if (containerSortingModes().remove(groupKey) != null) {
            ContainerData.INSTANCE.save();
        }
    }

    /**
     * Removes persisted filtering mode for a container key group.
     */
    private static void clearStoredFilteringModeForContainer(Set<String> keys) {
        String groupKey = containerGroupKey(keys);
        if (groupKey.isEmpty()) {
            return;
        }

        if (containerFilteringModes().remove(groupKey) != null) {
            ContainerData.INSTANCE.save();
        }
    }

    /**
     * @return {@code true} when the block entity at the position is a shulker box.
     */
    private static boolean isShulker(Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity;
    }

    /**
     * Resolves (or creates) cached runtime state for a shulker position.
     */
    private static ShulkerState shulkerState(BlockPos pos) {
        return shulkerStateCache.computeIfAbsent(pos.immutable(), ignored -> new ShulkerState());
    }

    /**
     * @return all position keys that represent this container.
     * Large chests include both halves so either side maps to the same tracked state.
     */
    private static Set<String> keysForContainer(Level level, BlockPos pos) {
        Set<String> keys = new HashSet<>();
        keys.add(key(level, pos));

        // Return default keys of the level is not a chest block entity
        if (!(level.getBlockEntity(pos) instanceof ChestBlockEntity)) {
            return keys;
        }

        BlockState state = level.getBlockState(pos);
        // Ensure state is a chest block
        if (!(state.getBlock() instanceof ChestBlock)) {
            return keys;
        }

        ChestType chestType = state.getValue(ChestBlock.TYPE);
        // If chest block is single, simply return default keys
        if (chestType == ChestType.SINGLE) {
            return keys;
        }

        // Determine the direction of facing chests, to ensure double chests store their keys for each part of the chest
        Direction facing = state.getValue(ChestBlock.FACING);
        Direction partnerDirection = chestType == ChestType.LEFT
                ? facing.getClockWise()
                : facing.getCounterClockWise();
        BlockPos partnerPos = pos.relative(partnerDirection);
        BlockState partnerState = level.getBlockState(partnerPos);
        if (!(partnerState.getBlock() instanceof ChestBlock)) {
            return keys;
        }

        // Final determinations for double chests
        ChestType expectedPartnerType = chestType == ChestType.LEFT ? ChestType.RIGHT : ChestType.LEFT;
        if (partnerState.getValue(ChestBlock.TYPE) != expectedPartnerType) {
            return keys;
        }
        if (partnerState.getValue(ChestBlock.FACING) != facing) {
            return keys;
        }
        if (!(level.getBlockEntity(partnerPos) instanceof ChestBlockEntity)) {
            return keys;
        }

        // Add the keys to the chest's partner position
        keys.add(key(level, partnerPos));
        return keys;
    }

    /**
     * Toggles tracking state for a chest.
     *
     * @return {@code true} if now tracked after toggling.
     */
    public static boolean toggleTracked(Level level, BlockPos pos) {
        // Shulker tracking
        if (isShulker(level, pos)) {
            BlockPos immutablePos = pos.immutable();
            ShulkerState state = shulkerState(immutablePos);
            state.filtered = !state.filtered;
            if (!state.filtered) {
                String shulkerKey = shulkerSortKey(immutablePos);
                if (!shulkerKey.isEmpty()) {
                    containerSortingModes().remove(shulkerKey);
                    containerFilteringModes().remove(shulkerKey);
                    ContainerData.INSTANCE.save();
                }
            }
            // Store the current filtering mode from the shulker pos
            if (activeShulkerPos != null && activeShulkerPos.equals(immutablePos)) {
                CURRENT_FILTER_TYPE = state.tagFiltered ? FilteringType.TAG : FilteringType.ITEM;
            }
            // Store the active shulker pos as the shulker's position
            activeShulkerPos = immutablePos;
            // Send the data to the server
            pushActiveShulkerStateToServer();
            // Return if the shulker is filtered
            return state.filtered;
        }

        // For non-shulkers, get the keys for the container
        Set<String> keys = keysForContainer(level, pos);
        boolean tracked;
        // Cleanup data
        if (keys.stream().anyMatch(k -> itemFilteredContainers().contains(k)) || keys.stream().anyMatch(k -> tagFilteredContainers().contains(k))) {
            itemFilteredContainers().removeAll(keys);
            tagFilteredContainers().removeAll(keys);
            clearStoredSortModeForContainer(keys);
            clearStoredFilteringModeForContainer(keys);
            tracked = false;
        } else {
            itemFilteredContainers().addAll(keys);
            tagFilteredContainers().removeAll(keys); // default newly tracked containers to item-filtered
            tracked = true;
        }
        // Save the data
        ContainerData.INSTANCE.save();
        // Return if the container is tracked
        return tracked;
    }

    /**
     * Remembers the chest that was just interacted with.
     */
    public static void rememberOpened(Level level, BlockPos pos) {
        // Shulker data-tracking
        if (isShulker(level, pos)) {
            BlockPos immutablePos = pos.immutable();
            pendingOpenedShulkerPos = immutablePos;
            pendingOpenedContainerKeys = null;

            // Prefer already-known runtime state so we do not regress to stale client-side block-entity data.
            if (!shulkerStateCache.containsKey(immutablePos)) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof ShulkerStateHolder holder) {
                    ShulkerState state = shulkerState(immutablePos);
                    state.filtered = holder.isFiltered();
                    state.tagFiltered = holder.isTagFiltered();
                    state.filterItems = holder.getFilterItems();
                    state.lockedSlots = holder.getLockedSlots();
                    state.sortingMode = holder.getSortingMode();
                }
            }

            // Send the packet to get the shulker's data from the server (if possible)
            Balm.networking().sendToServer(new RequestShulkerStateC2SPacket(immutablePos));
            return;
        }
        // Remove pending shulker pos, set current container keys
        pendingOpenedContainerKeys = keysForContainer(level, pos);
        pendingOpenedShulkerPos = null;
    }

    /**
     * @return {@code true} if the last opened chest was tracked.
     */
    public static boolean consumePendingOpenIsTracked() {
        // If the last shulker pos was tracked
        if (pendingOpenedShulkerPos != null) {
            // Remember the pending shulker pos, clear all other keys
            activeShulkerPos = pendingOpenedShulkerPos;
            pendingOpenedShulkerPos = null;
            activeContainerKeys = null;

            // Set the filter mode
            ShulkerState state = shulkerStateCache.get(activeShulkerPos);
            if (state == null) {
                CURRENT_FILTER_TYPE = FilteringType.ITEM;
                CURRENT_FILTER_MODE = FilteringMode.MATCHING;
                return false;
            }

            // Set the current filtering mode, and if the shulker is a tracked container
            CURRENT_FILTER_TYPE = state.tagFiltered ? FilteringType.TAG : FilteringType.ITEM;
            IS_TRACKED_CONTAINER = state.filtered;
            // Apply changes, return if filtered
            applyActiveTrackedFilteringMode();
            applyActiveTrackedSortMode();
            return state.filtered;
        }

        // Return false if there is no pending container key
        if (pendingOpenedContainerKeys == null) {
            return false;
        }

        // Get the active container keys from the pending container keys
        activeContainerKeys = new HashSet<>(pendingOpenedContainerKeys);
        // Determine tracked and tag filtered
        boolean tag = activeContainerKeys.stream().anyMatch(k -> tagFilteredContainers().contains(k));
        boolean tracked = activeContainerKeys.stream().anyMatch(k -> itemFilteredContainers().contains(k)) || tag;
        // Store the current filter mode for the container
        CURRENT_FILTER_TYPE = tag ? FilteringType.TAG : FilteringType.ITEM;
        // Apply changes, return if filtered
        pendingOpenedContainerKeys = null;
        IS_TRACKED_CONTAINER = tracked;
        applyActiveTrackedFilteringMode();
        applyActiveTrackedSortMode();
        return tracked;
    }

    /**
     * Cycles filtering mode for the currently opened tracked container.
     */
    public static void cycleCurrentFilteringMode() {
        if (!IS_TRACKED_CONTAINER) {
            return;
        }
        CURRENT_FILTER_MODE = switch (CURRENT_FILTER_MODE) {
            case NONE -> FilteringMode.MATCHING;
            case MATCHING -> FilteringMode.CURRENT_STACKS;
            case CURRENT_STACKS -> FilteringMode.MATCHING;
        };
        storeActiveFilteringMode(CURRENT_FILTER_MODE);
    }

    /**
     * Toggles filter mode for the currently opened tracked container.
     */
    public static void toggleCurrentFilterMode() {
        // If the shulker pos isn't null
        if (activeShulkerPos != null) {
            // Get the shulker state, return out if it's not filtered
            ShulkerState state = shulkerState(activeShulkerPos);
            if (!state.filtered) {
                return;
            }
            // Set filtering modes, send to server, return out
            state.tagFiltered = !state.tagFiltered;
            CURRENT_FILTER_TYPE = state.tagFiltered ? FilteringType.TAG : FilteringType.ITEM;
            pushActiveShulkerStateToServer();
            return;
        }

        // Handle item filtering
        if (CURRENT_FILTER_TYPE == FilteringType.ITEM) {
            itemFilteredContainers().removeAll(activeContainerKeys);
            tagFilteredContainers().addAll(activeContainerKeys);
            CURRENT_FILTER_TYPE = FilteringType.TAG;
        } else { // Handle tag filtering
            tagFilteredContainers().removeAll(activeContainerKeys);
            itemFilteredContainers().addAll(activeContainerKeys);
            CURRENT_FILTER_TYPE = FilteringType.ITEM;
        }
        // Save the data
        ContainerData.INSTANCE.save();
    }

    /**
     * Toggles a locked slot for the currently opened context.
     *
     * @param containerSlot {@code true} for container slot storage, {@code false} for player inventory storage.
     * @param slot          the slot index.
     */
    public static void toggleLockedSlot(boolean containerSlot, int slot) {
        // For shulker boxes, special handling
        if (containerSlot && activeShulkerPos != null) {
            // Get the active shulker state
            ShulkerState state = shulkerState(activeShulkerPos);
            // Get a set of locked slots
            Set<Integer> slotSet = new TreeSet<>(state.lockedSlots);
            boolean locked;
            // If the slot is already locked, remove the locked slot from the box
            if (slotSet.contains(slot)) {
                slotSet.remove(slot);
                locked = false;
            } else { // Otherwise, lock the slot for the box
                slotSet.add(slot);
                locked = true;
            }
            // Set the shulker's locked slot to the new locked slot set
            state.lockedSlots = new ArrayList<>(slotSet);
            // Send the new data over to the server (if it can receive it)
            pushActiveShulkerStateToServer();
            // Play the locked slot sound, finished and return out
            playLockSlotSound(Minecraft.getInstance(), locked);
            return;
        }

        // For non-shulkers, basic handling
        // Get the key for a container; if a container, get container key; otherwise, if ender chest OR player inventory (most likely), get the world's key
        String key = containerSlot ? activeContainerGroupKey() : worldKey();
        // If the key is empty, return out
        if (key.isEmpty()) {
            return;
        }

        // Get the locked slots, for container or player
        Map<String, List<Integer>> slotsMap = containerSlot ? containerLockedSlotsMap() : playerLockedSlotsMap();
        // Create a new slot set for the container/player inventory
        Set<Integer> slotSet = new TreeSet<>(slotsMap.getOrDefault(key, Collections.emptyList()));
        boolean locked;
        if (slotSet.contains(slot)) { // If slot is already locked, unlock it
            slotSet.remove(slot);
            locked = false;
        } else { // Otherwise, lock it
            slotSet.add(slot);
            locked = true;
        }

        // If there are no locked slots, remove the key
        if (slotSet.isEmpty()) {
            slotsMap.remove(key);
        } else { // Otherwise, add it to the set
            slotsMap.put(key, new ArrayList<>(slotSet));
        }

        // Play the locked slot sound
        playLockSlotSound(Minecraft.getInstance(), locked);

        // Save, according to if container or player inventory
        if (containerSlot) {
            LockedContainerSlots.INSTANCE.save();
        } else {
            LockedPlayerSlots.INSTANCE.save();
        }
    }

    /**
     * @return locked slots for the currently opened context.
     */
    public static Set<Integer> getLockedSlots(boolean containerSlot) {
        // For shulker boxes, special handing
        if (containerSlot && activeShulkerPos != null) {
            // Get the active shulker state from the server
            ShulkerState state = shulkerStateCache.get(activeShulkerPos);
            // If there are no locked slots, return a simple empty set
            if (state == null || state.lockedSlots.isEmpty()) {
                return Collections.emptySet();
            }
            // Otherwise, return the shulker's locked slots from the server
            return new HashSet<>(state.lockedSlots);
        }

        // Get the locked slots from a container, or player inventory
        String key = containerSlot ? activeContainerGroupKey() : worldKey();
        // Return empty set if there are no locked slots
        if (key.isEmpty()) {
            return Collections.emptySet();
        }

        // Create a map for the container/player inventory, and locked slots
        Map<String, List<Integer>> slotsMap = containerSlot ? containerLockedSlotsMap() : playerLockedSlotsMap();
        List<Integer> slots = slotsMap.get(key);
        // If there are no locked slots, return empty collection
        if (slots == null || slots.isEmpty()) {
            return Collections.emptySet();
        }
        // Otherwise, return the set of locked slots
        return new HashSet<>(slots);
    }

    /**
     * @return placeholder stacks for currently active tracked container.
     */
    public static List<ItemStack> getCurrentPlaceholderStacks() {
        // For shulker boxes, special handing for placeholder items
        if (activeShulkerPos != null) {
            // Create a temporary new list
            List<ItemStack> stacks = new ArrayList<>();
            // Get the active shulker state
            ShulkerState state = shulkerStateCache.get(activeShulkerPos);
            // If there are no current filter items, return the empty stacks list
            if (state == null || state.filterItems.isEmpty()) {
                return stacks;
            }
            // Otherwise, begin iterating through the shulker's filtered items
            for (String id : state.filterItems) {
                // If string item ID is blank or null, continue to next iteration
                if (id == null || id.isBlank()) {
                    continue;
                }
                // Attempt to parse the string item ID to a new ItemStack
                try {
                    Identifier identifier = Identifier.parse(id);
                    Optional<Holder.Reference<Item>> item = BuiltInRegistries.ITEM.get(identifier);
                    // If the stack is valid, add it to the new filtered items stacks list as a new ItemStack
                    if (item.isPresent() && item.get().value() != Items.AIR) {
                        stacks.add(new ItemStack(item.get().value()));
                    }
                } catch (Exception ignored) { // If failed, result in exception, but ignore
                }
            }
            // Return the newly created filtered items stacks list for the box
            return stacks;
        }

        // For containers, create a new filtered items stacks list
        List<ItemStack> stacks = new ArrayList<>();
        // Get the key for the active container
        String key = activeContainerGroupKey();
        // If the key is empty, return the empty stacks list, because there are no filtered items in an empty key!
        if (key.isEmpty()) {
            return stacks;
        }

        // Create a list of all container filter item IDs
        List<String> ids = containerFilterItems().get(key);
        // Return the blank list if the id list is empty (or null, somehow)
        if (ids == null || ids.isEmpty()) {
            return stacks;
        }

        // Begin iterating through all item IDs
        for (String id : ids) {
            // If ID is blank or null, continue to next iteration
            if (id == null || id.isBlank()) {
                continue;
            }

            // Attempt to parse the item ID as a new ItemStack
            try {
                Identifier identifier = Identifier.parse(id);
                Optional<Holder.Reference<Item>> item = BuiltInRegistries.ITEM.get(identifier);
                // Add it to the stacks list if the item is present
                if (item.isPresent() && item.get().value() != Items.AIR) {
                    stacks.add(new ItemStack(item.get().value()));
                }
            } catch (Exception ignored) { // If exception is created, ignore
            }
        }
        // Return the new filtered items stacks list for a container
        return stacks;
    }

    /**
     * Saves placeholder stacks for currently active tracked container.
     */
    public static void setCurrentPlaceholderStacks(List<ItemStack> stacks) {
        // For shulker boxes, special handling
        if (activeShulkerPos != null) {
            // Get the active shulker state
            ShulkerState state = shulkerState(activeShulkerPos);
            // Create new hash set for item IDs
            Set<String> ids = new LinkedHashSet<>();
            // For each stack in the list, add it to the IDs list (if valid)
            for (ItemStack stack : stacks) {
                if (stack == null || stack.isEmpty()) {
                    continue;
                }
                Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
                ids.add(id.toString());
            }
            // Set the filter items to the new list
            state.filterItems = new ArrayList<>(ids);
            // Send the data to the server (if possible), done and return out
            pushActiveShulkerStateToServer();
            return;
        }

        // Get the container key, if empty, return out, because no filtered items
        String key = activeContainerGroupKey();
        if (key.isEmpty()) {
            return;
        }

        // Iterate through all item IDs and add to new list
        Set<String> ids = new LinkedHashSet<>();
        for (ItemStack stack : stacks) {
            if (stack == null || stack.isEmpty()) {
                continue;
            }

            Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
            ids.add(id.toString());
        }

        // If the list is empty, remove the blank filtered items list from the container data
        if (ids.isEmpty()) {
            containerFilterItems().remove(key);
        } else { // Otherwise, add/update it
            containerFilterItems().put(key, new ArrayList<>(ids));
        }
        // Save the data
        ContainerData.INSTANCE.save();
    }

    /**
     * Pushes the current active shulker cache state to the server.
     */
    private static void pushActiveShulkerStateToServer() {
        // Don't do anything if the active shulker pos is null
        if (activeShulkerPos == null) {
            return;
        }
        // Get the active shulker state, and send all data to the server (if possible)
        ShulkerState state = shulkerState(activeShulkerPos);
        Balm.networking().sendToServer(new UpdateShulkerStateC2SPacket(
                activeShulkerPos,
                state.filtered,
                state.tagFiltered,
                itemsToCsv(state.filterItems),
                intsToCsv(state.lockedSlots),
                state.sortingMode
        ));
    }

    /**
     * Applies a server-authoritative shulker state sync packet to local runtime cache.
     */
    public static void applySyncedShulkerState(SyncShulkerStateS2CPacket packet) {
        // Get all shulker data from the sync packet (server to client)
        BlockPos pos = packet.pos().immutable();
        ShulkerState state = shulkerState(pos);
        state.filtered = packet.filtered();
        state.tagFiltered = packet.tagMode();
        state.filterItems = parseItemsCsv(packet.filterItemsCsv());
        state.lockedSlots = parseIntsCsv(packet.lockedSlotsCsv());
        state.sortingMode = packet.sortingMode();

        // Apply the data to the client, from server
        if (activeShulkerPos != null && activeShulkerPos.equals(pos)) {
            IS_TRACKED_CONTAINER = state.filtered;
            CURRENT_FILTER_TYPE = state.tagFiltered ? FilteringType.TAG : FilteringType.ITEM;
            applyActiveTrackedSortMode();
        }
    }

    /**
     * Serializes item identifier values to comma-separated form.
     */
    protected static String itemsToCsv(List<String> items) {
        if (items == null || items.isEmpty()) {
            return "";
        }
        return String.join(",", items);
    }

    /**
     * Serializes integer values to comma-separated form.
     */
    private static String intsToCsv(List<Integer> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        return values.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    /**
     * Parses comma-separated item identifiers.
     */
    private static List<String> parseItemsCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Parses comma-separated integer values.
     */
    private static List<Integer> parseIntsCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            return new ArrayList<>();
        }

        List<Integer> values = new ArrayList<>();
        for (String part : csv.split(",")) {
            String trimmed = part.trim();
            if (trimmed.isBlank()) {
                continue;
            }
            try {
                values.add(Integer.parseInt(trimmed));
            } catch (NumberFormatException ignored) {
            }
        }

        return values.stream()
                .distinct()
                .sorted()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * @return normalized string form of the current dimension.
     */
    private static String dimensionKey(Level level) {
        return level.dimension() == Level.END ? "minecraft:end"
                : level.dimension() == Level.NETHER ? "minecraft:nether"
                  : level.dimension() == Level.OVERWORLD ? "minecraft:overworld" : level.dimension().toString();
    }

    /**
     * @return a world/session key so container tracking does not collide across saves/servers.
     */
    public static String worldKey() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getSingleplayerServer() != null) {
            return "singleplayer:" + minecraft.getSingleplayerServer().getWorldPath(LevelResource.ROOT).getParent().getFileName().toString();
        }

        if (minecraft.getCurrentServer() != null && !minecraft.getCurrentServer().ip.isBlank()) {
            return "server:" + minecraft.getCurrentServer().ip.toLowerCase(Locale.ROOT);
        }

        return "unknown_world";
    }

    /**
     * @return the key for the tracked chests.
     */
    private static String key(Level level, BlockPos pos) {
        String world = worldKey();
        String dimension = dimensionKey(level);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof EnderChestBlockEntity) {
            // Ender chest inventory is shared, so use one world-scoped key regardless of block position or dimension.
            return world + " / minecraft:ender_chest";
        }
        String container = blockEntity instanceof BarrelBlockEntity ? "minecraft:barrel"
                : blockEntity instanceof ChestBlockEntity ? "minecraft:chest" : "unknown_container";

        return world + " / " + dimension + " / " + container + " (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")";
    }

    /**
     * Runtime client cache for the currently known state of one shulker position.
     */
    private static final class ShulkerState {
        boolean filtered;
        boolean tagFiltered;
        List<String> filterItems = new ArrayList<>();
        List<Integer> lockedSlots = new ArrayList<>();
        String sortingMode = getDefaultSortingModeForContainer().name();
    }
}