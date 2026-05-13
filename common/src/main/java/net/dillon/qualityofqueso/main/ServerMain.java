package net.dillon.qualityofqueso.main;

import net.blay09.mods.balm.core.BalmRegistrars;

/**
 * The dedicated server-side entrypoint for Quality of Queso.
 */
public class ServerMain {

    public static void sInitialize(BalmRegistrars registrars) {
        ServerEvents.sendSuccessMessageForDedicatedServerFeatures();
    }
}