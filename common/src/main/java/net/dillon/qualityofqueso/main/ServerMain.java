package net.dillon.qualityofqueso.main;

/**
 * The dedicated server-side entrypoint for Quality of Queso.
 */
public class ServerMain {

    public static void sInitialize() {
        ServerEvents.sendSuccessMessageForDedicatedServerFeatures();
    }
}