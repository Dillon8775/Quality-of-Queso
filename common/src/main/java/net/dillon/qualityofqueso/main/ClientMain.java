package net.dillon.qualityofqueso.main;

import net.blay09.mods.balm.core.BalmRegistrars;
import net.dillon.qualityofqueso.option.base.BaseOptions;
import net.dillon.qualityofqueso.option.instance.ModClientOptions;
import net.dillon.qualityofqueso.option.instance.TrackedContainers;

import static net.dillon.qualityofqueso.debug.ModHudEntries.registerDebugEntries;
import static net.dillon.qualityofqueso.util.ModUtil.error;
import static net.dillon.qualityofqueso.util.ModUtil.stop;

/**
 * The client-side entrypoint for Quality of Queso.
 */
public class ClientMain {

    public static void cInitialize(BalmRegistrars registrars) {
        registerDebugEntries();

        checkClientConfigs();
    }

    private static void checkClientConfigs() {
        boolean shouldStop = false;
        String configName = "";
        if (ModClientOptions.CLIENT.getInstance() == null) {
            error("Quality of Queso's client-config is null! Please delete it and relaunch your game.");
            shouldStop = true;
            configName = BaseOptions.DEFAULT_CLIENT_FILE_NAME;
        }
        if (TrackedContainers.TRACKED_CONTAINERS.getInstance() == null) {
            error("Quality of Queso's tracked containers config is null! Please delete it and relaunch your game.");
            shouldStop = true;
            configName = BaseOptions.DEFAULT_TRACKED_CONTAINERS_NAME;
        }

        stop(shouldStop, configName);
    }
}