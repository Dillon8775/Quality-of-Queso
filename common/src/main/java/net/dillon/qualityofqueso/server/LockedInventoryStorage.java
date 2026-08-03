package net.dillon.qualityofqueso.server;

import net.dillon.dillonlib.annotation.Dill;
import net.dillon.dillonlib.annotation.DillType;
import net.dillon.dillonlib.util.PlayerStorage;

import java.util.UUID;

/**
 * Stores player data for their locked inventory.
 */
@Dill(DillType.COMMON)
public class LockedInventoryStorage extends PlayerStorage<String> {

    public LockedInventoryStorage(String defaultValue) {
        super(defaultValue);
    }

    @Override
    protected void onSet(UUID playerUuid, String lockInventory) {
        if (lockInventory.equals("UNLOCKED")) {
            DedicatedServerStorage.PENDING_MANUAL_PICKUP.remove(playerUuid);
        }
    }

    /**
     * @return if a player's inventory is locked.
     */
    public static boolean isLockedInventory(UUID playerUuid) {
        return !DedicatedServerStorage.LOCKED_INVENTORY.get(playerUuid).equals("UNLOCKED");
    }

    /**
     * @return if a player's inventory is soft-locked.
     */
    public static boolean isSoftLockedInventory(UUID playerUuid) {
        return DedicatedServerStorage.LOCKED_INVENTORY.get(playerUuid).equals("SOFT_LOCKED");
    }
}