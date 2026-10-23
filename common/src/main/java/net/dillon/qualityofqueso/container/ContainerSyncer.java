package net.dillon.qualityofqueso.container;

import net.dillon.qualityofqueso.container.data.ContainerSortModes;
import net.dillon.qualityofqueso.container.data.FilteredContainer;
import net.dillon.qualityofqueso.container.data.FilteredContainerData;
import net.dillon.qualityofqueso.container.data.LockedContainerSlotData;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.option.ContainerData;
import net.dillon.qualityofqueso.option.LockedContainerSlots;
import net.dillon.qualityofqueso.option.LockedPlayerSlots;
import net.dillon.qualityofqueso.option.eum.management.FilteringMode;
import net.dillon.qualityofqueso.option.eum.management.sorting.CurrentSortingMode;
import net.dillon.qualityofqueso.option.eum.management.sorting.DefaultSortingMode;
import net.dillon.qualityofqueso.option.eum.management.sorting.Sorting;
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

import static net.dillon.dillonlib.task.ClientTasks.getMinecraft;
import static net.dillon.qualityofqueso.container.ShulkerSyncer.*;
import static net.dillon.qualityofqueso.helper.SoundHelper.playLockSlotSound;
import static net.dillon.qualityofqueso.option.OptionInstances.*;
import static net.dillon.qualityofqueso.option.eum.management.FilteringMode.unfiltered;

/**
 * Tracks client-selected containers.
 */
public class ContainerSyncer {

    /**
     * Filtering types for containers.
     */
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
    public static boolean CAN_PLAY_INACTIVE_SOUND = true;

    public static FilteringType CURRENT_FILTER_TYPE = FilteringType.ITEM;
    public static FilteringMode CURRENT_FILTER_MODE = FilteringMode.MATCHING;

    private static boolean capturedGlobalSortingModeForActiveContainer = false;

    protected static Set<FilteredContainer> pendingOpenedContainerKeys = null;
    protected static Set<FilteredContainer> activeContainerKeys = null;

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
        return blockEntity instanceof ChestBlockEntity
                || blockEntity instanceof EnderChestBlockEntity
                || blockEntity instanceof ShulkerBoxBlockEntity
                || blockEntity instanceof BarrelBlockEntity;
    }

    /**
     * @return if the specified mode is unfiltered.
     */
    protected static boolean isUnfiltered(String mode) {
        return mode == null || mode.isBlank() || mode.equals(unfiltered());
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
        ShulkerSyncer.pendingOpenedShulkerPos = null;
    }

    /**
     * @return the stored container data for each world.
     */
    private static Map<String, List<FilteredContainerData>> filteredContainers() {
        if (containerData().filteredContainers == null) {
            containerData().filteredContainers = new HashMap<>();
        }

        return containerData().filteredContainers;
    }

    private static Map<String, List<ContainerSortModes>> containerSortingModes() {
        if (containerData().sortingModes == null) {
            containerData().sortingModes = new HashMap<>();
        }

        return containerData().sortingModes;
    }

    /**
     * @return the stored container data for the current world.
     */
    private static List<FilteredContainerData> filteredContainersForWorld() {
        return filteredContainers().computeIfAbsent(
                worldKey(),
                ignored -> new ArrayList<>()
        );
    }

    private static List<ContainerSortModes> sortingModesForWorld() {
        return containerSortingModes().computeIfAbsent(worldKey(), key -> new ArrayList<>());
    }

    /**
     * Finds the stored {@code container data} for a container in the current world.
     */
    private static FilteredContainerData findContainerData(FilteredContainer target) {
        if (target == null) {
            return null;
        }

        return filteredContainersForWorld().stream()
                .filter(Objects::nonNull)
                .filter(entry -> entry.container != null)
                .filter(entry -> entry.container.matches(target))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds the stored {@code sorting mode} data for a container the current world.
     */
    private static ContainerSortModes findSortingModeData(FilteredContainer target) {
        if (target == null) {
            return null;
        }

        return sortingModesForWorld().stream()
                .filter(Objects::nonNull)
                .filter(data -> data.container != null)
                .filter(data -> data.container.matches(target))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds locked-slot data matching a container identity in the current world.
     */
    private static LockedContainerSlotData findLockedContainerSlotData(FilteredContainer target) {
        if (target == null) {
            return null;
        }

        return lockedContainerSlots().lockedContainerSlots
                .computeIfAbsent(worldKey(), ignored -> new ArrayList<>())
                .stream()
                .filter(Objects::nonNull)
                .filter(data -> data.container != null)
                .filter(data -> data.container.matches(target))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets the stored data for a container, creating it if necessary.
     */
    private static FilteredContainerData getOrCreateContainerData(FilteredContainer target) {
        FilteredContainerData data = findContainerData(target);

        if (data != null) {
            return data;
        }

        data = new FilteredContainerData();
        data.container = target;

        filteredContainersForWorld().add(data);

        return data;
    }

    /**
     * @return the {@link ContainerSortModes} for the current container.
     */
    private static ContainerSortModes getOrCreateSortingModeData(FilteredContainer container) {
        ContainerSortModes data = findSortingModeData(container);

        if (data == null) {
            data = new ContainerSortModes();
            data.container = container;
            sortingModesForWorld().add(data);
        }

        return data;
    }

    /**
     * Saves the container data.
     */
    private static void saveContainerData() {
        ContainerData.INSTANCE.save();
    }

    /**
     * Checks whether a string is null, empty, or whitespace.
     */
    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Removes any empty container data.
     */
    private static void removeEmptyContainerData(FilteredContainerData data) {
        if (data.filteringType.isEmpty()
                && data.filteringMode.isEmpty()
                && data.filterItems.isEmpty()) {
            filteredContainersForWorld().remove(data);
        }
    }

    /**
     * Removes any empty sorting mode data.
     */
    private static void removeEmptySortingModeData(ContainerSortModes data) {
        if (data.sortingMode.isEmpty()) {
            sortingModesForWorld().remove(data);
        }
    }

    /**
     * @return the filtered containers matching the specified filtering type.
     */
    private static Map<String, List<FilteredContainer>> filteredContainers(FilteringType filterType) {
        Map<String, List<FilteredContainer>> result = new HashMap<>();

        filteredContainers().forEach((world, entries) -> {
            List<FilteredContainer> filtered = entries.stream()
                    .filter(Objects::nonNull)
                    .filter(entry -> filterType.name().equals(entry.filteringType))
                    .map(entry -> entry.container)
                    .filter(Objects::nonNull)
                    .toList();

            result.put(world, new ArrayList<>(filtered));
        });

        return result;
    }

    /**
     * @return the tracked item-filtering containers.
     */
    private static Map<String, List<FilteredContainer>> itemFilteredContainers() {
        return filteredContainers(FilteringType.ITEM);
    }

    /**
     * @return the tracked tag-filtering containers.
     */
    private static Map<String, List<FilteredContainer>> tagFilteredContainers() {
        return filteredContainers(FilteringType.TAG);
    }

    /**
     * Adds or updates a container's filtering type.
     */
    private static void addContainer(FilteredContainer container, FilteringType type) {
        if (container == null) {
            return;
        }

        FilteredContainerData data = getOrCreateContainerData(container);

        if (!type.name().equals(data.filteringType)) {
            data.filteringType = type.name();
            saveContainerData();
        }
    }

    /**
     * Removes a container's filtering type without deleting its other saved settings.
     */
    private static void removeContainer(FilteredContainer container, FilteringType type) {
        FilteredContainerData data = findContainerData(container);

        if (data == null || !type.name().equals(data.filteringType)) {
            return;
        }

        data.filteringType = "";
        removeEmptyContainerData(data);
        saveContainerData();
    }

    /**
     * Gets the container list belonging to the current world.
     */
    private static List<FilteredContainer> filteredContainers(Map<String, List<FilteredContainer>> data) {
        return data.computeIfAbsent(worldKey(), ignored -> new ArrayList<>());
    }

    /**
     * Checks whether an identity exists in a list.
     */
    private static boolean containsContainer(Map<String, List<FilteredContainer>> data, FilteredContainer target) {
        return filteredContainers(data).stream()
                .filter(Objects::nonNull)
                .anyMatch(container -> container.matches(target));
    }

    /**
     * Checks whether every identity is present in a collection.
     */
    private static boolean containsAllContainers(Map<String, List<FilteredContainer>> data, Set<FilteredContainer> targets) {
        return targets.stream().allMatch(target -> containsContainer(data, target));
    }

    /**
     * Checks whether any identity is present in a collection.
     */
    private static boolean containsAnyContainer(Map<String, List<FilteredContainer>> data, Set<FilteredContainer> targets) {
        return targets.stream().anyMatch(target -> containsContainer(data, target));
    }

    /**
     * Gets a stored sorting or filtering mode for a container.
     */
    private static String getContainerMode(FilteredContainer container, boolean sortingMode) {
        if (sortingMode) {
            ContainerSortModes data = findSortingModeData(container);
            return data != null ? data.sortingMode : "";
        }

        FilteredContainerData data = findContainerData(container);
        return data != null ? data.filteringMode : "";
    }

    /**
     * Gets a stored sorting or filtering mode for multiple containers (ex. a double chest).
     */
    private static String getContainerMode(Set<FilteredContainer> containers, boolean sortingMode) {
        return containers.stream()
                .map(container -> getContainerMode(container, sortingMode))
                .filter(mode -> !mode.isEmpty())
                .findFirst()
                .orElse("");
    }

    /**
     * Stores a sorting or filtering mode for a container.
     */
    private static void setContainerMode(FilteredContainer container, boolean sortingMode, String mode) {
        if (sortingMode) {
            ContainerSortModes data = getOrCreateSortingModeData(container);
            data.sortingMode = mode;

            removeEmptySortingModeData(data);
        } else {
            FilteredContainerData data = getOrCreateContainerData(container);
            data.filteringMode = mode;

            removeEmptyContainerData(data);
        }

        saveContainerData();
    }

    /**
     * Stores a sorting or filtering mode for multiple containers (ex. a double chest).
     */
    private static void setContainerMode(Set<FilteredContainer> containers, boolean sortingMode, String mode) {
        for (FilteredContainer container : containers) {
            setContainerMode(container, sortingMode, mode);
        }
    }

    /**
     * Clears a sorting or filtering mode without deleting the container's other data.
     */
    private static void clearContainerModes(Set<FilteredContainer> containers, boolean sortingMode) {
        for (FilteredContainer container : containers) {
            if (sortingMode) {
                ContainerSortModes data = findSortingModeData(container);

                if (data != null) {
                    data.sortingMode = "";
                    removeEmptySortingModeData(data);
                }
            } else {
                FilteredContainerData data = findContainerData(container);

                if (data != null) {
                    data.filteringMode = "";
                    removeEmptyContainerData(data);
                }
            }
        }

        saveContainerData();
    }

    /**
     * Clears empty filtering modes for containers.
     */
    public static void clearStoredFilteringModeForContainer(Set<FilteredContainer> containers) {
        clearContainerModes(containers, false);
    }

    /**
     * @return if the box has an active sorting mode.
     */
    private static boolean hasActiveSortContext() {
        return activeShulkerPos != null
                || (activeContainerKeys != null && !activeContainerKeys.isEmpty());
    }

    /**
     * Applies saved sort mode for the active context.
     * If no mode is stored, resolves from current default and persists it.
     */
    protected static void applyActiveTrackedSortMode() {
        if (!hasActiveSortContext() || client().sorting().useGlobalSortingMode) {
            return;
        }

        if (!capturedGlobalSortingModeForActiveContainer) {
            ModConstants.GLOBAL_SORTING_MODE = client().sorting().currentSortingMode;
            capturedGlobalSortingModeForActiveContainer = true;
        }

        if (activeShulkerPos != null) {
            applyActiveSortModeForActiveShulker();
            return;
        }

        String stored = getContainerMode(activeContainerKeys, true);

        if (stored.isBlank()) {
            CurrentSortingMode mode = getDefaultSortingModeForContainer();
            client().sorting().currentSortingMode = mode;

            setContainerMode(activeContainerKeys, true, mode.name());
            return;
        }

        client().sorting().currentSortingMode = CurrentSortingMode.fromName(stored);
    }

    /**
     * @return the default sorting mode to use for unsorted containers.
     */
    public static CurrentSortingMode getDefaultSortingModeForContainer() {
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

        if (activeShulkerPos != null) {
            storeActiveSortModeForActiveShulker(mode);
            return;
        }

        setContainerMode(activeContainerKeys, true, mode.name());
    }

    /**
     * Applies saved filtering mode for the active tracked context.
     */
    protected static void applyActiveFilteringMode() {
        if (!IS_TRACKED_CONTAINER) {
            return;
        }

        if (activeShulkerPos != null) {
            applyActiveFilteringModeForActiveShulker();
            return;
        }

        String stored = getContainerMode(activeContainerKeys, false);

        if (stored.isBlank()) {
            CURRENT_FILTER_MODE = getDefaultFilteringModeForContainer();

            setContainerMode(activeContainerKeys, false, CURRENT_FILTER_MODE.name());
            return;
        }

        try {
            CURRENT_FILTER_MODE = FilteringMode.valueOf(stored);
        } catch (IllegalArgumentException ignored) {
            CURRENT_FILTER_MODE = getDefaultFilteringModeForContainer();
        }
    }

    /**
     * @return the default filtering mode to use for unconfigured containers.
     */
    protected static FilteringMode getDefaultFilteringModeForContainer() {
        return client().isFillingCurrentStacks()
                ? FilteringMode.CURRENT_STACKS
                : FilteringMode.MATCHING;
    }

    /**
     * Persists current filtering mode for the active tracked context.
     */
    private static void storeActiveFilteringMode(FilteringMode mode) {
        if (mode == null || !IS_TRACKED_CONTAINER) {
            return;
        }

        if (activeShulkerPos != null) {
            storeActiveFilteringModeForActiveShulker(mode);
            return;
        }

        if (activeContainerKeys == null || activeContainerKeys.isEmpty()) {
            return;
        }

        setContainerMode(activeContainerKeys, false, mode.name());
    }

    /**
     * @return all container identities, including both halves of a double chest.
     */
    private static Set<FilteredContainer> keysForContainer(Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof ChestBlockEntity)) {
            return Set.of(containerIdentity(level, pos));
        }

        BlockState state = level.getBlockState(pos);

        if (!(state.getBlock() instanceof ChestBlock)) {
            return Set.of(containerIdentity(level, pos));
        }

        ChestType chestType = state.getValue(ChestBlock.TYPE);

        if (chestType == ChestType.SINGLE) {
            return Set.of(containerIdentity(level, pos));
        }

        Direction facing = state.getValue(ChestBlock.FACING);
        Direction partnerDirection = chestType == ChestType.LEFT
                ? facing.getClockWise()
                : facing.getCounterClockWise();

        BlockPos partnerPos = pos.relative(partnerDirection);
        BlockState partnerState = level.getBlockState(partnerPos);

        if (!(partnerState.getBlock() instanceof ChestBlock)) {
            return Set.of(containerIdentity(level, pos));
        }

        ChestType expectedType = chestType == ChestType.LEFT
                ? ChestType.RIGHT
                : ChestType.LEFT;

        if (partnerState.getValue(ChestBlock.TYPE) != expectedType
                || partnerState.getValue(ChestBlock.FACING) != facing
                || !(level.getBlockEntity(partnerPos) instanceof ChestBlockEntity)) {
            return Set.of(containerIdentity(level, pos));
        }

        FilteredContainer doubleChest = new FilteredContainer(
                "minecraft:chest",
                dimensionKey(level),
                pos,
                partnerPos
        );

        mergeDoubleChestData(doubleChest);

        return Set.of(doubleChest);
    }

    /**
     * Checks whether a container identity contains a specific position.
     */
    private static boolean containsPosition(
            FilteredContainer container,
            int[] targetPos
    ) {
        return container != null
                && container.positions != null
                && targetPos != null
                && container.positions.stream()
                .anyMatch(position -> Arrays.equals(position, targetPos));
    }

    /**
     * Checks whether a stored container identity belongs to either half
     * of the specified double chest.
     */
    private static boolean belongsToDoubleChest(
            FilteredContainer stored,
            FilteredContainer doubleChest
    ) {
        if (stored == null
                || doubleChest == null
                || !Objects.equals(stored.type, doubleChest.type)
                || !Objects.equals(stored.dimension, doubleChest.dimension)
                || stored.positions == null
                || doubleChest.positions == null) {
            return false;
        }

        return doubleChest.positions.stream()
                .anyMatch(position -> containsPosition(stored, position));
    }

    /**
     * Merges previously separate records for the halves of a double chest.
     */
    private static void mergeDoubleChestData(FilteredContainer doubleChest) {
        if (doubleChest == null
                || doubleChest.positions == null
                || doubleChest.positions.size() != 2) {
            return;
        }

        List<FilteredContainerData> filteringEntries = filteredContainersForWorld();

        List<FilteredContainerData> matchingFilteringEntries = filteringEntries.stream()
                .filter(Objects::nonNull)
                .filter(entry -> belongsToDoubleChest(entry.container, doubleChest))
                .toList();

        if (!matchingFilteringEntries.isEmpty()) {
            FilteredContainerData merged = matchingFilteringEntries.getFirst();

            for (int i = 1; i < matchingFilteringEntries.size(); i++) {
                FilteredContainerData entry = matchingFilteringEntries.get(i);

                if (isBlank(merged.filteringType)) {
                    merged.filteringType = entry.filteringType;
                }

                if (isBlank(merged.filteringMode)) {
                    merged.filteringMode = entry.filteringMode;
                }

                if (entry.filterItems != null) {
                    if (merged.filterItems == null) {
                        merged.filterItems = new ArrayList<>();
                    }

                    for (String item : entry.filterItems) {
                        if (!merged.filterItems.contains(item)) {
                            merged.filterItems.add(item);
                        }
                    }
                }

                if (entry != merged) {
                    filteringEntries.remove(entry);
                }
            }

            merged.container = doubleChest;
        }

        List<ContainerSortModes> sortingEntries = sortingModesForWorld();

        List<ContainerSortModes> matchingSortingEntries = sortingEntries.stream()
                .filter(Objects::nonNull)
                .filter(entry -> belongsToDoubleChest(entry.container, doubleChest))
                .toList();

        if (!matchingSortingEntries.isEmpty()) {
            ContainerSortModes merged = matchingSortingEntries.getFirst();

            for (int i = 1; i < matchingSortingEntries.size(); i++) {
                ContainerSortModes entry = matchingSortingEntries.get(i);

                if (isBlank(merged.sortingMode)) {
                    merged.sortingMode = entry.sortingMode;
                }

                if (entry != merged) {
                    sortingEntries.remove(entry);
                }
            }

            merged.container = doubleChest;
        }

        if (!matchingFilteringEntries.isEmpty() || !matchingSortingEntries.isEmpty()) {
            saveContainerData();
        }
    }

    /**
     * @return if tracked after toggling tracking state for container.
     */
    public static boolean toggleTracked(Level level, BlockPos pos) {
        if (isShulker(level, pos)) {
            return toggleTrackedForActiveShulker(level, pos);
        }

        Set<FilteredContainer> containers = keysForContainer(level, pos);

        boolean tracked = containsAnyContainer(itemFilteredContainers(), containers)
                || containsAnyContainer(tagFilteredContainers(), containers);

        if (tracked) {
            for (FilteredContainer container : containers) {
                removeContainer(container, FilteringType.ITEM);
                removeContainer(container, FilteringType.TAG);
            }

            clearStoredFilteringModeForContainer(containers);
        } else {
            for (FilteredContainer container : containers) {
                addContainer(container, FilteringType.ITEM);
            }
        }

        saveContainerData();
        return !tracked;
    }

    /**
     * Remembers the chest that was just interacted with.
     */
    public static void rememberOpened(Level level, BlockPos pos) {
        if (isShulker(level, pos)) {
            rememberOpenedForShulker(level, pos);
            return;
        }

        pendingOpenedContainerKeys = keysForContainer(level, pos);
        pendingOpenedShulkerPos = null;
    }

    /**
     * Consumes a container as opened and applies correct filtering and modes.
     */
    public static boolean consumePendingOpenIsTracked() {
        if (pendingOpenedShulkerPos != null) {
            return consumePendingOpenedIsTrackedForActiveShulker();
        }

        if (pendingOpenedContainerKeys == null) {
            return false;
        }

        activeContainerKeys = new LinkedHashSet<>(pendingOpenedContainerKeys);
        pendingOpenedContainerKeys = null;
        activeShulkerPos = null;

        boolean itemTracked = containsAnyContainer(
                itemFilteredContainers(), activeContainerKeys
        );
        boolean tagTracked = containsAnyContainer(
                tagFilteredContainers(), activeContainerKeys
        );

        IS_TRACKED_CONTAINER = itemTracked || tagTracked;

        boolean allTagFiltered = IS_TRACKED_CONTAINER
                && containsAllContainers(tagFilteredContainers(), activeContainerKeys);

        CURRENT_FILTER_TYPE = allTagFiltered
                ? FilteringType.TAG
                : FilteringType.ITEM;

        if (IS_TRACKED_CONTAINER) {
            applyActiveFilteringMode();
        }

        applyActiveTrackedSortMode();

        return IS_TRACKED_CONTAINER;
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
     * Toggles filter type for the currently opened tracked container.
     */
    public static void toggleCurrentFilterMode() {
        if (toggleCurrentFilterModeForActiveShulker()) {
            return;
        }

        if (!IS_TRACKED_CONTAINER
                || activeContainerKeys == null
                || activeContainerKeys.isEmpty()) {
            return;
        }

        FilteringType destination = CURRENT_FILTER_TYPE == FilteringType.ITEM
                ? FilteringType.TAG
                : FilteringType.ITEM;

        for (FilteredContainer container : activeContainerKeys) {
            removeContainer(container, CURRENT_FILTER_TYPE);
            addContainer(container, destination);
        }

        CURRENT_FILTER_TYPE = destination;

        saveContainerData();
    }

    /**
     * Locks a container and prevents it from using the sort function.
     */
    public static void lockContainer() {
        if (activeShulkerPos != null) {
            lockActiveShulker();
            return;
        }

        if (activeContainerKeys == null || activeContainerKeys.isEmpty()) {
            return;
        }

        FilteredContainer container = activeContainerKeys.iterator().next();

        if (container == null) {
            return;
        }

        ContainerSortModes data = findSortingModeData(container);

        if (data == null) {
            data = new ContainerSortModes();
            data.container = container;
            data.locked = true;
            sortingModesForWorld().add(data);
        } else {
            data.locked = !data.locked;
        }

        CAN_PLAY_INACTIVE_SOUND = data.locked;
        playLockSlotSound(data.locked);
        ContainerData.INSTANCE.save();
    }

    /**
     * @return if the active container is locked.
     */
    public static boolean isActiveContainerLocked() {
        if (client().sorting().sorting != Sorting.BUTTON_OR_KEY) {
            return false;
        }

        if (isActiveShulkerLocked()) {
            return true;
        }

        if (activeContainerKeys == null || activeContainerKeys.isEmpty()) {
            return false;
        }

        FilteredContainer container = activeContainerKeys.iterator().next();
        ContainerSortModes data = findSortingModeData(container);

        return data != null && data.locked;
    }

    /**
     * Toggles a locked slot for the currently opened container.
     * @param containerSlot {@code true} for container slot storage, {@code false} for player inventory storage.
     * @param slot the slot index.
     */
    public static void toggleLockedSlot(boolean containerSlot, int slot) {
        if (containerSlot && activeShulkerPos != null) {
            toggleLockedSlotForActiveShulker(slot);
            return;
        }

        if (containerSlot) {
            if (activeContainerKeys == null || activeContainerKeys.isEmpty()) {
                return;
            }

            // A double chest has one identity containing both block positions
            FilteredContainer container = activeContainerKeys.iterator().next();
            LockedContainerSlotData data = findLockedContainerSlotData(container);

            if (data == null) {
                data = new LockedContainerSlotData();
                data.container = container;
                lockedContainerSlots().lockedContainerSlots
                        .computeIfAbsent(worldKey(), ignored -> new ArrayList<>())
                        .add(data);
            }

            if (data.slots == null) {
                data.slots = new HashSet<>();
            }

            boolean locked = data.slots.add(slot);

            if (!locked) {
                data.slots.remove(slot);
            }

            if (data.slots.isEmpty()) {
                lockedContainerSlots().lockedContainerSlots
                        .getOrDefault(worldKey(), new ArrayList<>())
                        .remove(data);
            }

            List<LockedContainerSlotData> entries = lockedContainerSlots().lockedContainerSlots.get(worldKey());

            if (entries != null && entries.isEmpty()) {
                lockedContainerSlots().lockedContainerSlots.remove(worldKey());
            }

            playLockSlotSound(locked);
            LockedContainerSlots.INSTANCE.save();
            return;
        }

        String key = worldKey();
        Map<String, List<Integer>> slotsMap = playerLockedSlotsMap();

        Set<Integer> slotSet = new TreeSet<>(
                slotsMap.getOrDefault(key, Collections.emptyList())
        );

        boolean locked = slotSet.add(slot);

        if (!locked) {
            slotSet.remove(slot);
        }

        if (slotSet.isEmpty()) {
            slotsMap.remove(key);
        } else {
            slotsMap.put(key, new ArrayList<>(slotSet));
        }

        playLockSlotSound(locked);
        LockedPlayerSlots.INSTANCE.save();
    }

    /**
     * @return locked slots for the currently opened context.
     */
    public static Set<Integer> getLockedSlots(boolean containerSlot) {
        if (containerSlot && activeShulkerPos != null) {
            return getLockedSlotsForActiveShulker();
        }

        if (containerSlot) {
            if (activeContainerKeys == null || activeContainerKeys.isEmpty()) {
                return Collections.emptySet();
            }

            Set<Integer> slots = new HashSet<>();

            for (FilteredContainer container : activeContainerKeys) {
                LockedContainerSlotData data = findLockedContainerSlotData(container);

                if (data != null && data.slots != null) {
                    slots.addAll(data.slots);
                }
            }

            return slots;
        }

        List<Integer> slots = playerLockedSlotsMap().get(worldKey());

        if (slots == null || slots.isEmpty()) {
            return Collections.emptySet();
        }

        return new HashSet<>(slots);
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
     * Gets current placeholder stacks for a filtered container.
     */
    public static List<ItemStack> getCurrentPlaceholderStacks() {
        if (activeShulkerPos != null) {
            return getCurrentPlaceholderStacksForActiveShulker();
        }

        List<String> ids = new ArrayList<>();

        if (activeContainerKeys != null) {
            for (FilteredContainer container : activeContainerKeys) {
                FilteredContainerData data = findContainerData(container);

                if (data != null && data.filterItems != null) {
                    ids.addAll(data.filterItems);
                }
            }
        }

        return itemStacksFromIds(ids);
    }

    /**
     * Gets all item stacks based on their IDs.
     */
    protected static List<ItemStack> itemStacksFromIds(List<String> ids) {
        List<ItemStack> stacks = new ArrayList<>();

        if (ids == null) {
            return stacks;
        }

        for (String id : new LinkedHashSet<>(ids)) {
            if (id == null || id.isBlank()) {
                continue;
            }

            try {
                Optional<Holder.Reference<Item>> item = BuiltInRegistries.ITEM.get(Identifier.parse(id));

                if (item.isPresent() && item.get().value() != Items.AIR) {
                    stacks.add(new ItemStack(item.get().value()));
                }
            } catch (Exception ignored) {
            }
        }

        stacks.sort(Comparator.comparing(stack -> stack.getItemName().getString()));
        return stacks;
    }

    /**
     * Sets filtered items for a container.
     */
    private static void setPlaceholderItems(FilteredContainer target, List<String> ids) {
        if (target == null) {
            return;
        }

        FilteredContainerData data = findContainerData(target);

        if (ids == null || ids.isEmpty()) {
            if (data != null && data.filterItems != null && !data.filterItems.isEmpty()) {
                data.filterItems.clear();
                removeEmptyContainerData(data);
            }

            return;
        }

        data = getOrCreateContainerData(target);
        data.filterItems = new ArrayList<>(new LinkedHashSet<>(ids));
    }

    /**
     * Sets new placeholder items for a container in a screen.
     */
    public static void setCurrentPlaceholderStacks(List<ItemStack> stacks) {
        Set<String> ids = new LinkedHashSet<>();

        for (ItemStack stack : stacks) {
            if (stack != null && !stack.isEmpty()) {
                ids.add(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
            }
        }

        if (activeShulkerPos != null) {
            setCurrentPlaceholderStacksForActiveShulker(ids);
            return;
        }

        if (activeContainerKeys == null) {
            return;
        }

        for (FilteredContainer container : activeContainerKeys) {
            setPlaceholderItems(container, new ArrayList<>(ids));
        }

        saveContainerData();
    }

    /**
     * @return normalized string form of the current dimension.
     */
    private static String dimensionKey(Level level) {
        return level.dimension().identifier().toString();
    }

    /**
     * @return a world/session key so container tracking does not collide across saves/servers.
     */
    public static String worldKey() {
        Minecraft minecraft = getMinecraft();

        if (minecraft.getSingleplayerServer() != null) {
            return "singleplayer:"
                    + minecraft.getSingleplayerServer()
                    .getWorldPath(LevelResource.ROOT)
                    .getParent()
                    .getFileName()
                    .toString();
        }

        if (minecraft.getCurrentServer() != null
                && !minecraft.getCurrentServer().ip.isBlank()) {
            return "server:"
                    + minecraft.getCurrentServer().ip.toLowerCase(Locale.ROOT);
        }

        return "unknown_world";
    }

    /**
     * Creates an identity for a container.
     */
    protected static FilteredContainer containerIdentity(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof EnderChestBlockEntity) {
            FilteredContainer container = new FilteredContainer();
            container.type = "minecraft:ender_chest";
            container.dimension = "";
            container.positions = new ArrayList<>();
            return container;
        }

        String type = blockEntity instanceof BarrelBlockEntity
                ? "minecraft:barrel"
                : blockEntity instanceof ChestBlockEntity
                ? "minecraft:chest"
                : "unknown_container";

        return new FilteredContainer(type, dimensionKey(level), pos);
    }
}