package net.dillon.qualityofqueso.server;

import net.dillon.dillonlib.annotation.Dill;
import net.dillon.dillonlib.annotation.DillType;
import net.dillon.dillonlib.util.PlayerStorage;

import java.util.UUID;

/**
 * Stores server-synced client options and cached values.
 */
@Dill(DillType.COMMON)
public class DedicatedServerStorage {
    public static final PlayerStorage<Boolean> INCLUDE_HOTBAR = new PlayerStorage<>(false);
    public static final PlayerStorage<Boolean> PERPENDICULAR_QUICK_MOVING = new PlayerStorage<>(false);
    public static final PlayerStorage<String> LOCKED_INVENTORY = new PlayerStorage<>("UNLOCKED");
    public static final PlayerStorage<PendingManualPickup> PENDING_MANUAL_PICKUP = new PlayerStorage<>(null);

    /**
     * Consumes a pending manual pickup allowance when it matches entity and is still valid.
     */
    public static boolean consumeManualPickup(UUID playerUuid, int entityId, long currentTick) {
        PendingManualPickup pending = PENDING_MANUAL_PICKUP.get(playerUuid);
        if (pending == null || pending.entityId() != entityId || currentTick > pending.expiresAtTick()) {
            return false;
        }

        PENDING_MANUAL_PICKUP.remove(playerUuid);
        return true;
    }
}