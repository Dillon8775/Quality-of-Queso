package net.dillon.qualityofqueso.container;

import net.blay09.mods.balm.Balm;
import net.dillon.qualityofqueso.container.data.FilteredContainer;
import net.dillon.qualityofqueso.container.data.ShulkerState;
import net.dillon.qualityofqueso.option.eum.management.FilteringMode;
import net.dillon.qualityofqueso.option.eum.management.sorting.CurrentSortingMode;
import net.dillon.qualityofqueso.packet.clientbound.SyncShulkerStateS2CPacket;
import net.dillon.qualityofqueso.packet.serverbound.RequestShulkerStateC2SPacket;
import net.dillon.qualityofqueso.packet.serverbound.UpdateShulkerStateC2SPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;

import java.util.*;
import java.util.stream.Collectors;

import static net.dillon.qualityofqueso.container.ContainerSyncer.*;
import static net.dillon.qualityofqueso.helper.SoundHelper.playLockSlotSound;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.eum.management.FilteringMode.unfiltered;

/**
 * Tracks client-to-server selected shulker boxes.
 */
public class ShulkerSyncer {

    protected static boolean activeShulkerStateSynced = false;
    protected static BlockPos pendingOpenedShulkerPos = null;
    protected static BlockPos activeShulkerPos = null;

    protected static final Map<BlockPos, ShulkerState> shulkerStateCache = new HashMap<>();

    /**
     * @return {@code true} when the block entity at the position is a shulker box.
     */
    protected static boolean isShulker(Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity;
    }

    /**
     * Resolves or creates cached runtime state for a shulker position.
     */
    protected static ShulkerState shulkerState(BlockPos pos) {
        return shulkerStateCache.computeIfAbsent(
                pos.immutable(),
                ignored -> new ShulkerState()
        );
    }

    /**
     * Applies the active sort mode for a shulker.
     */
    protected static void applyActiveSortModeForActiveShulker() {
        ShulkerState state = shulkerStateCache.get(activeShulkerPos);
        String stored = state == null ? null : state.sortingMode;

        if (stored == null || stored.isBlank()) {
            CurrentSortingMode mode = getDefaultSortingModeForContainer();
            client().sorting().currentSortingMode = mode;

            if (state != null) {
                state.sortingMode = mode.name();
                pushActiveShulkerStateToServer();
            }
        } else {
            client().sorting().currentSortingMode =
                    CurrentSortingMode.fromName(stored);
        }
    }

    /**
     * Applies the active filtering mode for a shulker.
     */
    protected static void applyActiveFilteringModeForActiveShulker() {
        ShulkerState state = shulkerStateCache.get(activeShulkerPos);

        if (state == null || isUnfiltered(state.filteringMode)) {
            CURRENT_FILTER_MODE = getDefaultFilteringModeForContainer();
        } else {
            try {
                CURRENT_FILTER_MODE = FilteringMode.valueOf(state.filteringMode);
            } catch (IllegalArgumentException ignored) {
                CURRENT_FILTER_MODE = getDefaultFilteringModeForContainer();
            }
        }
    }

    /**
     * Stores the active sort mode for a shulker.
     */
    protected static void storeActiveSortModeForActiveShulker(CurrentSortingMode mode) {
        ShulkerState state = shulkerState(activeShulkerPos);
        state.sortingMode = mode.name();
        pushActiveShulkerStateToServer();
    }

    /**
     * Stores the active filtering mode for a shulker.
     */
    protected static void storeActiveFilteringModeForActiveShulker(FilteringMode mode) {
        ShulkerState state = shulkerState(activeShulkerPos);
        state.filteringMode = mode.name();
        pushActiveShulkerStateToServer();
    }

    /**
     * @return if tracked after toggling tracking state for a shulker.
     */
    protected static boolean toggleTrackedForActiveShulker(Level level, BlockPos pos) {
        BlockPos immutablePos = pos.immutable();
        ShulkerState state = shulkerState(immutablePos);

        boolean tracked = isUnfiltered(state.filteringMode);

        state.filteringMode = tracked
                ? getDefaultFilteringModeForContainer().name()
                : unfiltered();

        if (!tracked) {
            Set<FilteredContainer> identity = Set.of(
                    containerIdentity(level, immutablePos)
            );

            clearStoredFilteringModeForContainer(identity);
        }

        if (immutablePos.equals(activeShulkerPos)) {
            CURRENT_FILTER_TYPE = state.tagFiltered
                    ? FilteringType.TAG
                    : FilteringType.ITEM;

            pushActiveShulkerStateToServer();
        }

        return !isUnfiltered(state.filteringMode);
    }

    /**
     * @return if a shulker opened applies correct filtering and modes.
     */
    protected static boolean consumePendingOpenedIsTrackedForActiveShulker() {
        activeShulkerPos = pendingOpenedShulkerPos;
        pendingOpenedShulkerPos = null;
        activeContainerKeys = null;

        ShulkerState state = shulkerStateCache.get(activeShulkerPos);

        if (state == null) {
            IS_TRACKED_CONTAINER = false;
            CURRENT_FILTER_TYPE = FilteringType.ITEM;
            CURRENT_FILTER_MODE = FilteringMode.MATCHING;
            return false;
        }

        CURRENT_FILTER_TYPE = state.tagFiltered
                ? FilteringType.TAG
                : FilteringType.ITEM;

        IS_TRACKED_CONTAINER = !isUnfiltered(state.filteringMode);

        applyActiveFilteringMode();
        applyActiveTrackedSortMode();

        return IS_TRACKED_CONTAINER;
    }

    /**
     * @return if the current filter mode for the shulker was changed.
     */
    protected static boolean toggleCurrentFilterModeForActiveShulker() {
        if (activeShulkerPos == null) {
            return false;
        }

        ShulkerState state = shulkerState(activeShulkerPos);

        if (isUnfiltered(state.filteringMode)) {
            return false;
        }

        state.tagFiltered = !state.tagFiltered;
        CURRENT_FILTER_TYPE = state.tagFiltered
                ? FilteringType.TAG
                : FilteringType.ITEM;

        pushActiveShulkerStateToServer();
        return true;
    }

    /**
     * Locks a shulker and prevents it from using the sort function.
     */
    protected static void lockActiveShulker() {
        ShulkerState state = shulkerState(activeShulkerPos);
        state.locked = !state.locked;
        pushActiveShulkerStateToServer();
        CAN_PLAY_INACTIVE_SOUND = state.locked;
        playLockSlotSound(state.locked);
    }

    /**
     * @return if the active shulker pos is locked.
     */
    protected static boolean isActiveShulkerLocked() {
        if (activeShulkerPos != null) {
            ShulkerState state = shulkerState(activeShulkerPos);
            return state.locked;
        }

        return false;
    }

    /**
     * Toggles a locked slot for a currently opened shulker.
     * @param slot the slot index.
     */
    protected static void toggleLockedSlotForActiveShulker(int slot) {
        ShulkerState state = shulkerState(activeShulkerPos);
        Set<Integer> slotSet = new TreeSet<>(state.lockedSlots);

        boolean locked = slotSet.add(slot);

        if (!locked) {
            slotSet.remove(slot);
        }

        state.lockedSlots = new ArrayList<>(slotSet);
        pushActiveShulkerStateToServer();
        playLockSlotSound(locked);
    }

    /**
     * @return the set of locked slots for a shulker.
     */
    protected static Set<Integer> getLockedSlotsForActiveShulker() {
        ShulkerState state = shulkerStateCache.get(activeShulkerPos);

        if (state == null || state.lockedSlots.isEmpty()) {
            return Collections.emptySet();
        }

        return new HashSet<>(state.lockedSlots);
    }

    /**
     * Gets current placeholder stacks for a filtered shulker box.
     */
    protected static List<ItemStack> getCurrentPlaceholderStacksForActiveShulker() {
        ShulkerState state = shulkerStateCache.get(activeShulkerPos);

        return state == null
                ? new ArrayList<>()
                : itemStacksFromIds(state.filterItems);
    }

    /**
     * Sets new placeholder items for a shulker box.
     */
    protected static void setCurrentPlaceholderStacksForActiveShulker(Set<String> ids) {
        ShulkerState state = shulkerState(activeShulkerPos);
        state.filterItems = new ArrayList<>(ids);
        pushActiveShulkerStateToServer();
    }

    /**
     * Pushes the current active shulker cache state to the server.
     */
    protected static void pushActiveShulkerStateToServer() {
        if (activeShulkerPos == null || !activeShulkerStateSynced) {
            return;
        }

        ShulkerState state = shulkerState(activeShulkerPos);

        Balm.networking().sendToServer(new UpdateShulkerStateC2SPacket(
                activeShulkerPos,
                state.sortingMode,
                state.filteringMode,
                state.tagFiltered,
                state.locked,
                itemsToCsv(state.filterItems),
                intsToCsv(state.lockedSlots)
        ));
    }

    /**
     * Remembers a shulker that was opened.
     */
    protected static void rememberOpenedForShulker(Level level, BlockPos pos) {
        BlockPos immutablePos = pos.immutable();

        activeShulkerStateSynced = false;
        pendingOpenedShulkerPos = immutablePos;
        pendingOpenedContainerKeys = null;

        if (!shulkerStateCache.containsKey(immutablePos)) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof ShulkerBlockEntityHolder shulkerBlockEntityHolder) {
                ShulkerState state = shulkerState(immutablePos);

                ShulkerBlockEntityHolder.writeShulkerState(
                        state,
                        shulkerBlockEntityHolder.getSortingMode(),
                        shulkerBlockEntityHolder.getFilteringMode(),
                        shulkerBlockEntityHolder.isTagFiltered(),
                        shulkerBlockEntityHolder.isLocked(),
                        shulkerBlockEntityHolder.getFilterItems(),
                        shulkerBlockEntityHolder.getLockedSlots()
                );
            }
        }

        Balm.networking().sendToServer(
                new RequestShulkerStateC2SPacket(immutablePos)
        );
    }

    /**
     * Applies a server-authoritative shulker state sync packet to local runtime cache.
     */
    public static void applySyncedShulkerState(SyncShulkerStateS2CPacket packet) {
        BlockPos pos = packet.pos().immutable();
        ShulkerState state = shulkerState(pos);

        ShulkerBlockEntityHolder.writeShulkerState(
                state,
                packet.sortingMode(),
                packet.filteringMode(),
                packet.tagMode(),
                packet.locked(),
                parseItemsCsv(packet.filterItemsCsv()),
                parseIntsCsv(packet.lockedSlotsCsv())
        );

        if (activeShulkerPos != null && activeShulkerPos.equals(pos)) {
            activeShulkerStateSynced = true;

            ContainerSyncer.IS_TRACKED_CONTAINER =
                    !isUnfiltered(state.filteringMode);

            ContainerSyncer.CURRENT_FILTER_TYPE = state.tagFiltered
                    ? FilteringType.TAG
                    : FilteringType.ITEM;

            if (ContainerSyncer.IS_TRACKED_CONTAINER) {
                applyActiveFilteringMode();
            }

            applyActiveTrackedSortMode();
        }
    }

    /**
     * Serializes item identifiers to comma-separated text.
     */
    public static String itemsToCsv(List<String> items) {
        if (items == null || items.isEmpty()) {
            return "";
        }
        return String.join(",", items);
    }

    /**
     * Serializes integer values to comma-separated text.
     */
    public static String intsToCsv(List<Integer> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        return values.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    /**
     * Parses comma-separated item identifiers.
     */
    public static List<String> parseItemsCsv(String csv) {
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
    public static List<Integer> parseIntsCsv(String csv) {
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
}