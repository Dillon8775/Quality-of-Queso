package net.dillon.qualityofqueso.main;

import static net.dillon.qualityofqueso.helper.ModHelper.info;

/**
 * Dedicated server-side events for Quality of Queso.
 */
public final class ServerEvents {
    private static final String MESSAGE = "Quality of Queso features including item frame searching and locked slots inside shulker boxes are now available for all players connecting to this server!";

    public static void sendSuccessMessageForDedicatedServerFeatures() {
        info(MESSAGE);
    }
}