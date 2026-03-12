package net.dillon.qualityofqueso.util;

import net.dillon.qualityofqueso.option.instance.TrackedContainers;
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
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

import java.util.*;

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
    public static boolean OPENING_PLACEHOLDER_SCREEN = false;
    public static boolean RETURNING_FROM_PLACEHOLDER_SCREEN = false;
    public static FilterMode CURRENT_FILTER_MODE = FilterMode.ITEM;
    private static Set<String> pendingOpenedContainerKeys = null;
    private static Set<String> activeContainerKeys = null;

    /**
     * @return the tracked chests list.
     */
    private static Set<String> itemFilteredContainers() {
        if (trackedContainers().itemFilteredContainers == null) {
            trackedContainers().itemFilteredContainers = new HashSet<>();
        }
        return trackedContainers().itemFilteredContainers;
    }

    /**
     * @return the tracked tag-filtered container list.
     */
    private static Set<String> tagFilteredContainers() {
        if (trackedContainers().tagFilteredContainers == null) {
            trackedContainers().tagFilteredContainers = new HashSet<>();
        }
        return trackedContainers().tagFilteredContainers;
    }

    /**
     * @return the tracked placeholder item IDs by container key group.
     */
    private static Map<String, List<String>> containerFilterItems() {
        if (trackedContainers().containerFilterItems == null) {
            trackedContainers().containerFilterItems = new HashMap<>();
        }
        return trackedContainers().containerFilterItems;
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
        String world = worldKey(level);
        String dimension = level.dimension() == Level.END ? "minecraft:end"
                : level.dimension() == Level.NETHER ? "minecraft:nether"
                : level.dimension() == Level.OVERWORLD ? "minecraft:overworld" : level.dimension().toString();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        String container = blockEntity instanceof ShulkerBoxBlockEntity ? "minecraft:shulker_box"
                : blockEntity instanceof BarrelBlockEntity ? "minecraft:barrel"
                : blockEntity instanceof ChestBlockEntity ? "minecraft:chest" : "unknown_container";
        return world + " / " + dimension + " / " + container + " (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")";
    }

    /**
     * @return a world/session key so container tracking does not collide across saves/servers.
     */
    private static String worldKey(Level level) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getSingleplayerServer() != null) {
            return "singleplayer:" + minecraft.getSingleplayerServer().getWorldData().getLevelName();
        }

        if (minecraft.getCurrentServer() != null && !minecraft.getCurrentServer().ip.isBlank()) {
            return "server:" + minecraft.getCurrentServer().ip.toLowerCase(Locale.ROOT);
        }

        return "unknown_world";
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
        if (keys.stream().anyMatch(k -> itemFilteredContainers().contains(k)) || keys.stream().anyMatch(k -> tagFilteredContainers().contains(k))) {
            itemFilteredContainers().removeAll(keys);
            tagFilteredContainers().removeAll(keys);
            tracked = false;
        } else {
            itemFilteredContainers().addAll(keys);
            tagFilteredContainers().removeAll(keys); // default newly tracked containers to item-filtered
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
        boolean tag = activeContainerKeys.stream().anyMatch(k -> tagFilteredContainers().contains(k));
        boolean tracked = activeContainerKeys.stream().anyMatch(k -> itemFilteredContainers().contains(k)) || tag;
        CURRENT_FILTER_MODE = tag ? FilterMode.TAG : FilterMode.ITEM;
        pendingOpenedContainerKeys = null;
        return tracked;
    }

    /**
     * Toggles filter mode for the currently opened tracked container.
     */
    public static void toggleCurrentFilterMode() {
        if (CURRENT_FILTER_MODE == FilterMode.ITEM) {
            itemFilteredContainers().removeAll(activeContainerKeys);
            tagFilteredContainers().addAll(activeContainerKeys);
            CURRENT_FILTER_MODE = FilterMode.TAG;
        } else {
            tagFilteredContainers().removeAll(activeContainerKeys);
            itemFilteredContainers().addAll(activeContainerKeys);
            CURRENT_FILTER_MODE = FilterMode.ITEM;
        }
        TrackedContainers.TRACKED_CONTAINERS.save();
    }

    /**
     * @return current active container key group used for placeholder persistence.
     */
    private static String activeContainerGroupKey() {
        if (activeContainerKeys == null || activeContainerKeys.isEmpty()) {
            return "";
        }

        List<String> sorted = new ArrayList<>(activeContainerKeys);
        sorted.sort(Comparator.naturalOrder());
        return String.join(";", sorted);
    }

    /**
     * @return placeholder stacks for currently active tracked container.
     */
    public static List<ItemStack> getCurrentPlaceholderStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        String key = activeContainerGroupKey();
        if (key.isEmpty()) {
            return stacks;
        }

        List<String> ids = containerFilterItems().get(key);
        if (ids == null || ids.isEmpty()) {
            return stacks;
        }

        for (String id : ids) {
            if (id == null || id.isBlank()) {
                continue;
            }

            try {
                Identifier identifier = Identifier.parse(id);
                Optional<Holder.Reference<Item>> item = BuiltInRegistries.ITEM.get(identifier);
                if (item.isPresent() && item.get().value() != Items.AIR) {
                    stacks.add(new ItemStack(item.get().value()));
                }
            } catch (Exception ignored) {
            }
        }
        return stacks;
    }

    /**
     * Saves placeholder stacks for currently active tracked container.
     */
    public static void setCurrentPlaceholderStacks(List<ItemStack> stacks) {
        String key = activeContainerGroupKey();
        if (key.isEmpty()) {
            return;
        }

        Set<String> ids = new LinkedHashSet<>();
        for (ItemStack stack : stacks) {
            if (stack == null || stack.isEmpty()) {
                continue;
            }

            Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (id != null) {
                ids.add(id.toString());
            }
        }

        if (ids.isEmpty()) {
            containerFilterItems().remove(key);
        } else {
            containerFilterItems().put(key, new ArrayList<>(ids));
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
