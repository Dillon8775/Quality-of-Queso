package net.dillon.qualityofqueso.server;

/**
 * Stores pending item pickups for specific entities.
 */
public record PendingManualPickup(int entityId, long expiresAtTick) {
}