package net.dillon.qualityofqueso.util;

import net.dillon.qualityofqueso.option.instance.TrackedContainers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

import java.util.HashSet;
import java.util.Set;

import static net.dillon.qualityofqueso.util.ModUtil.trackedContainers;

/**
 * Tracks client-selected container positions that should auto-enable fill-what's-present.
 */
public class ContainerTracker {
    public enum FilterMode {
        ITEM,
        TAG;

        public boolean tag() {
            return this == TAG;
        }
    }

    public static final int DEFAULT_COOLDOWN = 1;
    public static int COOLDOWN = DEFAULT_COOLDOWN;
    public static boolean IS_TRACKED_CONTAINER = false;
    public static FilterMode CURRENT_FILTER_MODE = FilterMode.ITEM;
    private static Set<String> pendingOpenedContainerKeys = null;
    private static Set<String> activeContainerKeys = null;

    /**
     * @return the tracked chests list.
     */
    private static Set<String> trackedChests() {
        if (trackedContainers().trackedFillWhatsPresetChests == null) {
            trackedContainers().trackedFillWhatsPresetChests = new HashSet<>();
        }
        return trackedContainers().trackedFillWhatsPresetChests;
    }

    /**
     * @return the tracked tag-filtered container list.
     */
    private static Set<String> tagFilteredChests() {
        if (trackedContainers().trackedFillWhatsPresetTagChests == null) {
            trackedContainers().trackedFillWhatsPresetTagChests = new HashSet<>();
        }
        return trackedContainers().trackedFillWhatsPresetTagChests;
    }

    /**
     * @return valid block entities for tracking.
     */
    public static boolean isValidBlockEntity(BlockEntity blockEntity) {
        return blockEntity instanceof ChestBlockEntity || blockEntity instanceof ShulkerBoxBlockEntity || blockEntity instanceof BarrelBlockEntity;
    }

    /**
     * @return the key for the tracked chests.
     */
    private static String key(Level level, BlockPos pos) {
        return level.dimension().toString() + "|" + pos.asLong();
    }

    /**
     * @return all position keys that represent this container.
     * Large chests include both halves so either side maps to the same tracked state.
     */
    private static Set<String> keysForContainer(Level level, BlockPos pos) {
        Set<String> keys = new HashSet<>();
        keys.add(key(level, pos));

        if (!(level.getBlockEntity(pos) instanceof ChestBlockEntity)) {
            return keys;
        }

        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof ChestBlock)) {
            return keys;
        }

        ChestType chestType = state.getValue(ChestBlock.TYPE);
        if (chestType == ChestType.SINGLE) {
            return keys;
        }

        Direction facing = state.getValue(ChestBlock.FACING);
        Direction partnerDirection = chestType == ChestType.LEFT
                ? facing.getClockWise()
                : facing.getCounterClockWise();
        BlockPos partnerPos = pos.relative(partnerDirection);
        BlockState partnerState = level.getBlockState(partnerPos);
        if (!(partnerState.getBlock() instanceof ChestBlock)) {
            return keys;
        }

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

        keys.add(key(level, partnerPos));
        return keys;
    }

    /**
     * Toggles tracking state for a chest.
     * @return {@code true} if now tracked after toggling.
     */
    public static boolean toggleTracked(Level level, BlockPos pos) {
        Set<String> keys = keysForContainer(level, pos);
        boolean tracked;
        if (keys.stream().anyMatch(k -> trackedChests().contains(k))) {
            trackedChests().removeAll(keys);
            tagFilteredChests().removeAll(keys);
            tracked = false;
        } else {
            trackedChests().addAll(keys);
            tagFilteredChests().removeAll(keys); // default newly tracked containers to item-filtered
            tracked = true;
        }
        TrackedContainers.TRACKED_CONTAINERS.save();
        return tracked;
    }

    /**
     * Remembers the chest that was just interacted with.
     */
    public static void rememberOpened(Level level, BlockPos pos) {
        pendingOpenedContainerKeys = keysForContainer(level, pos);
    }

    /**
     * Clears pending interaction state when no chest interaction occurred.
     */
    public static void clearPendingOpened() {
        pendingOpenedContainerKeys = null;
    }

    /**
     * @return {@code true} if the last opened chest was tracked.
     */
    public static boolean consumePendingOpenIsTracked() {
        if (pendingOpenedContainerKeys == null) {
            return false;
        }

        activeContainerKeys = new HashSet<>(pendingOpenedContainerKeys);
        boolean tracked = activeContainerKeys.stream().anyMatch(k -> trackedChests().contains(k));
        CURRENT_FILTER_MODE = tracked && activeContainerKeys.stream().anyMatch(k -> tagFilteredChests().contains(k))
                ? FilterMode.TAG
                : FilterMode.ITEM;
        pendingOpenedContainerKeys = null;
        return tracked;
    }

    /**
     * Toggles filter mode for the currently opened tracked container.
     * @return the new mode after toggle.
     */
    public static void toggleCurrentFilterMode() {
        if (CURRENT_FILTER_MODE == FilterMode.ITEM) {
            tagFilteredChests().addAll(activeContainerKeys);
            CURRENT_FILTER_MODE = FilterMode.TAG;
        } else {
            tagFilteredChests().removeAll(activeContainerKeys);
            CURRENT_FILTER_MODE = FilterMode.ITEM;
        }
        TrackedContainers.TRACKED_CONTAINERS.save();
    }

    /**
     * Clears active container state when closing a container screen.
     */
    public static void clearActiveContainer() {
        activeContainerKeys = null;
        CURRENT_FILTER_MODE = FilterMode.ITEM;
    }
}
