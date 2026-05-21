package net.dillon.qualityofqueso.server;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores server-synced client options and cached values.
 */
public class DedicatedServerStorage {
    private static final Map<UUID, Boolean> INCLUDE_HOTBAR = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> PERPENDICULAR_QUICK_MOVING = new ConcurrentHashMap<>();
    private static final Map<UUID, String> LOCKED_INVENTORY = new ConcurrentHashMap<>();
    private static final Map<UUID, PendingManualPickup> PENDING_MANUAL_PICKUP = new ConcurrentHashMap<>();

    private record PendingManualPickup(int entityId, long expiresAtTick) {
    }

    /**
     * Sets the value of include hotbar for the player.
     */
    public static void setIncludeHotbar(UUID playerUuid, boolean shouldIncludeHotbar) {
        INCLUDE_HOTBAR.put(playerUuid, shouldIncludeHotbar);
    }

    /**
     * Sets the value of perpendicular quick moving for the player.
     */
    public static void setPerpendicularQuickMoving(UUID playerUuid, boolean shouldUsePerpendicularQuickMoving) {
        PERPENDICULAR_QUICK_MOVING.put(playerUuid, shouldUsePerpendicularQuickMoving);
    }

    /**
     * Sets the value of locked inventory for a player.
     */
    public static void setLockedInventory(UUID playerUuid, String lockInventory) {
        LOCKED_INVENTORY.put(playerUuid, lockInventory);
        if (lockInventory.equals("UNLOCKED")) {
            PENDING_MANUAL_PICKUP.remove(playerUuid);
        }
    }

    /**
     * @return if the player is including their hotbar in transferring.
     */
    public static boolean shouldIncludeHotbar(UUID playerUuid) {
        return INCLUDE_HOTBAR.getOrDefault(playerUuid, true);
    }

    /**
     * @return if the player should use perpendicular quick moving.
     */
    public static boolean shouldUsePerpendicularQuickMoving(UUID playerUuid) {
        return PERPENDICULAR_QUICK_MOVING.getOrDefault(playerUuid, false);
    }

    /**
     * @return if a player's inventory is locked.
     */
    public static boolean isLockedInventory(UUID playerUuid) {
        return !getLockedInventory(playerUuid).equals("UNLOCKED");
    }

    /**
     * @return if a player's inventory is soft-locked.
     */
    public static boolean isSoftLockedInventory(UUID playerUuid) {
        return getLockedInventory(playerUuid).equals("SOFT_LOCKED");
    }

    /**
     * @return if the player should use a locked inventory.
     */
    private static String getLockedInventory(UUID playerUuid) {
        return LOCKED_INVENTORY.getOrDefault(playerUuid, "UNLOCKED");
    }

    /**
     * Allows exactly one manual pickup for one item entity until the given expiration tick.
     */
    public static void allowManualPickup(UUID playerUuid, int entityId, long expiresAtTick) {
        PENDING_MANUAL_PICKUP.put(playerUuid, new PendingManualPickup(entityId, expiresAtTick));
    }

    /**
     * Consumes a pending manual pickup allowance when it matches entity and is still valid.
     */
    public static boolean consumeManualPickup(UUID playerUuid, int entityId, long currentTick) {
        PendingManualPickup pending = PENDING_MANUAL_PICKUP.get(playerUuid);
        if (pending == null || pending.entityId != entityId || currentTick > pending.expiresAtTick) {
            return false;
        }

        PENDING_MANUAL_PICKUP.remove(playerUuid);
        return true;
    }
}