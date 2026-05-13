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
     * @return if the player should use perpendicular quick moving.
     */
    public static boolean shouldUsePerpendicularQuickMoving(UUID playerUuid) {
        return PERPENDICULAR_QUICK_MOVING.getOrDefault(playerUuid, false);
    }

    /**
     * @return if the player is including their hotbar in transferring.
     */
    public static boolean shouldIncludeHotbar(UUID playerUuid) {
        return INCLUDE_HOTBAR.getOrDefault(playerUuid, true);
    }
}