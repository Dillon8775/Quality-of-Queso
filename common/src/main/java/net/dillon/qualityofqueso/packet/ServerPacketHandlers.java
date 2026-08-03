package net.dillon.qualityofqueso.packet;

import net.blay09.mods.balm.Balm;
import net.dillon.dillonlib.annotation.Dill;
import net.dillon.dillonlib.annotation.DillType;
import net.dillon.qualityofqueso.option.ModCommonOptions;
import net.dillon.qualityofqueso.server.DedicatedServerStorage;
import net.dillon.qualityofqueso.server.LockedInventoryStorage;
import net.dillon.qualityofqueso.server.PendingManualPickup;
import net.dillon.qualityofqueso.util.GlowCountdown;
import net.dillon.qualityofqueso.util.ShulkerStateHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Server-safe packet handlers used during common packet registration.
 * This class must remain free of client-only Minecraft imports.
 */
@Dill(DillType.COMMON)
public class ServerPacketHandlers {

    /**
     * Plays feedback sound at the player's location.
     */
    private static void playSound(ServerPlayer player, SoundEvent sound, float volume) {
        player.level().playSound(null, player.blockPosition(), sound, SoundSource.PLAYERS, volume, 1.0F);
    }

    /**
     * Handles C2S glow-search packet payloads.
     */
    public static void sendGlowPacket(ServerPlayer player, GlowSearchC2SPacket packet) {
        handleGlowPacket(player, packet.query(), packet.matchCase(), packet.clear(), packet.timer(), packet.radius());
    }

    /**
     * Stores the sender's client preference values in dedicated server state.
     */
    public static void handleClientToServerOptions(ServerPlayer player, ClientPreferencesC2SPacket packet) {
        DedicatedServerStorage.INCLUDE_HOTBAR.set(player.getUUID(), packet.includeHotbar());
        DedicatedServerStorage.PERPENDICULAR_QUICK_MOVING.set(player.getUUID(), packet.perpendicularQuickMoving());
        DedicatedServerStorage.LOCKED_INVENTORY.set(player.getUUID(), packet.lockInventory());
    }

    /**
     * Performs manual pickup by allowing one-shot touch on the targeted item entity.
     */
    public static void handleManualItemPickupIntent(ServerPlayer player, ManualItemPickupC2SPacket packet) {
        if (!LockedInventoryStorage.isLockedInventory(player.getUUID()) || !player.isShiftKeyDown()) {
            return;
        }

        if (player.level().getEntity(packet.entityId()) instanceof ItemEntity itemEntity) {
            DedicatedServerStorage.PENDING_MANUAL_PICKUP.set(player.getUUID(), new PendingManualPickup(itemEntity.getId(), player.level().getGameTime() + 2L));
            itemEntity.playerTouch(player);
        }
    }

    /**
     * Responds to a shulker-state request with authoritative server data.
     */
    public static void handleRequestShulkerState(ServerPlayer player, RequestShulkerStateC2SPacket packet) {
        if (player == null || packet == null) {
            return;
        }

        BlockEntity blockEntity = player.level().getBlockEntity(packet.pos());
        if (!(blockEntity instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity)) {
            return;
        }
        if (!(shulkerBoxBlockEntity instanceof ShulkerStateHolder holder)) {
            return;
        }

        Balm.networking().sendTo(player, new SyncShulkerStateS2CPacket(
                packet.pos(),
                holder.isFiltered(),
                holder.isTagFiltered(),
                itemsToCsv(holder.getFilterItems()),
                intsToCsv(holder.getLockedSlots()),
                holder.getSortingMode()
        ));
    }

    /**
     * Applies incoming shulker-state updates and sends canonical state back to the player.
     */
    public static void handleUpdateShulkerState(ServerPlayer player, UpdateShulkerStateC2SPacket packet) {
        if (player == null || packet == null) {
            return;
        }

        BlockEntity blockEntity = player.level().getBlockEntity(packet.pos());
        if (!(blockEntity instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity)) {
            return;
        }
        if (!(shulkerBoxBlockEntity instanceof ShulkerStateHolder holder)) {
            return;
        }

        holder.setFiltered(packet.filtered());
        holder.setTagFiltered(packet.tagMode());
        holder.setFilterItems(parseItemsCsv(packet.filterItemsCsv()));
        holder.setLockedSlots(parseIntsCsv(packet.lockedSlotsCsv()));
        holder.setSortingMode(packet.sortingMode());
        shulkerBoxBlockEntity.setChanged();

        Balm.networking().sendTo(player, new SyncShulkerStateS2CPacket(
                packet.pos(),
                holder.isFiltered(),
                holder.isTagFiltered(),
                itemsToCsv(holder.getFilterItems()),
                intsToCsv(holder.getLockedSlots()),
                holder.getSortingMode()
        ));
    }

    /**
     * Bridges S2C shulker sync handling to the client-only handler without
     * directly referencing client classes from common/server classloading paths.
     */
    public static void handleSyncShulkerState(Player player, SyncShulkerStateS2CPacket packet) {
        if (player == null || packet == null || !player.level().isClientSide()) {
            return;
        }

        try {
            Class<?> handlerClass = Class.forName("net.dillon.qualityofqueso.packet.ClientPacketHandlers");
            handlerClass
                    .getMethod("handleSyncShulkerState", Player.class, SyncShulkerStateS2CPacket.class)
                    .invoke(null, player, packet);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to dispatch client shulker sync handler.", e);
        }
    }

    /**
     * Applies glow or clear behavior to nearby item frames based on payload options.
     */
    private static void handleGlowPacket(ServerPlayer player, String query, boolean matchCase, boolean clear, int timer,
                                         int radius) {
        ModCommonOptions options = ModCommonOptions.INSTANCE.getInstance();
        if (options == null || !options.itemFrameSearching) {
            return;
        }

        ServerLevel world = player.level();
        Vec3 playerPos = player.position();

        List<ItemFrame> nearbyFrames = world.getEntitiesOfClass(ItemFrame.class,
                new AABB(playerPos.add(-radius, -radius, -radius), playerPos.add(radius, radius, radius)),
                frame -> {
                    String[] terms = query.split(",");

                    if (clear) {
                        return true;
                    }

                    ItemStack stack = frame.getItem();
                    if (stack.isEmpty()) {
                        return false;
                    }

                    String rawItemName = stack.getHoverName().getString().toLowerCase();
                    for (String term : terms) {
                        String trimmed = term.trim().toLowerCase();
                        if (matchCase ? rawItemName.matches(trimmed) : rawItemName.contains(trimmed)) {
                            return true;
                        }
                    }
                    return false;
                });

        int searched = 0;
        for (ItemFrame frame : nearbyFrames) {
            boolean alreadyGlowing = frame.isCurrentlyGlowing();
            frame.setGlowingTag(!clear);
            searched++;
            if (clear && !alreadyGlowing) {
                searched--;
            }

            if (!clear && timer != 0) {
                ((GlowCountdown) frame).startGlowCountdown(timer * 20);
            }
        }

        if (clear) {
            player.sendSystemMessage(Component.translatable("qualityofqueso.item_frame_searcher.executed.cleared", searched), false);
            playSound(player, SoundEvents.PLAYER_SPLASH, 1.0F);
            return;
        }

        if (nearbyFrames.isEmpty()) {
            player.sendSystemMessage(matchCase ?
                    Component.translatable("qualityofqueso.item_frame_searcher.executed.found_none.match_case", searched, query) :
                    Component.translatable("qualityofqueso.item_frame_searcher.executed.found_none", searched, query), false);
            playSound(player, SoundEvents.NOTE_BLOCK_BASS.value(), 2.0F);
            return;
        }

        if (timer == 0) {
            player.sendSystemMessage(matchCase ?
                    Component.translatable("qualityofqueso.item_frame_searcher.executed.without_timer.match_case", searched, query) :
                    Component.translatable("qualityofqueso.item_frame_searcher.executed.without_timer", searched, query));
        } else {
            player.sendSystemMessage(matchCase ?
                    Component.translatable("qualityofqueso.item_frame_searcher.executed.with_timer.match_case", searched, query, timer) :
                    Component.translatable("qualityofqueso.item_frame_searcher.executed.with_timer", searched, query, timer), false);
        }
        playSound(player, SoundEvents.ARROW_HIT_PLAYER, 1.0F);
    }

    /**
     * Serializes item identifiers to comma-separated text.
     */
    private static String itemsToCsv(List<String> items) {
        if (items == null || items.isEmpty()) {
            return "";
        }
        return String.join(",", items);
    }

    /**
     * Serializes integer values to comma-separated text.
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
}