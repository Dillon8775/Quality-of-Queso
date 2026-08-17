package net.dillon.qualityofqueso.main;

import net.dillon.qualityofqueso.helper.ModConstants;

/**
 * Dedicated server-side events for Quality of Queso.
 */
public class ServerEvents {
    private static final String MESSAGE = "Quality of Queso features, including item frame searching, locking inventories and locked slots inside shulker boxes are now available for all players connecting to this server!";

    public static void sendSuccessMessageForDedicatedServerFeatures() {
        ModConstants.LOGGER.info(MESSAGE);
    }
}