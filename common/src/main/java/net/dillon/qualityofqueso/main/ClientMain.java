package net.dillon.qualityofqueso.main;

import net.blay09.mods.balm.core.BalmRegistrars;
import net.dillon.qualityofqueso.option.ContainerData;
import net.dillon.qualityofqueso.option.LockedContainerSlots;
import net.dillon.qualityofqueso.option.LockedPlayerSlots;
import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.util.ModConstants;

import static net.dillon.qualityofqueso.debug.ModDebugScreenEntries.registerDebugEntries;
import static net.dillon.qualityofqueso.helper.ModHelper.error;
import static net.dillon.qualityofqueso.helper.ModHelper.stop;

/**
 * The client-side entrypoint for Quality of Queso.
 */
public class ClientMain {

    /**
     * Initializes client-side features.
     */
    public static void cInitialize(BalmRegistrars registrars) {
        registerDebugEntries();

        checkClientConfigs();
        loadClientConfigs();
    }

    /**
     * Loads all client configs.
     */
    private static void loadClientConfigs() {
        ModClientOptions.INSTANCE.load();
        ContainerData.INSTANCE.load();
        LockedContainerSlots.INSTANCE.load();
        LockedPlayerSlots.INSTANCE.load();
    }

    /**
     * Ensures client-side configs are valid configs, and handles invalid configs if necessary.
     */
    private static void checkClientConfigs() {
        boolean shouldStop = false;
        String configName = "";
        if (ModClientOptions.INSTANCE.getInstance() == null) {
            error("Quality of Queso's client-config is null! Please delete it and relaunch your game.");
            shouldStop = true;
            configName = ModConstants.DEFAULT_CLIENT_CONFIG_FILE_NAME;
        }
        if (ContainerData.INSTANCE.getInstance() == null) {
            error("Quality of Queso's tracked containers config is null! Please delete it and relaunch your game.");
            shouldStop = true;
            configName = ModConstants.DEFAULT_CONTAINER_DATA_FILE_NAME;
        }
        if (LockedContainerSlots.INSTANCE.getInstance() == null) {
            error("Quality of Queso's locked container slots config is null! Please delete it and relaunch your game.");
            shouldStop = true;
            configName = ModConstants.DEFAULT_LOCKED_CONTAINER_SLOTS_FILE_NAME;
        }
        if (LockedPlayerSlots.INSTANCE.getInstance() == null) {
            error("Quality of Queso's locked player slots config is null! Please delete it and relaunch your game.");
            shouldStop = true;
            configName = ModConstants.DEFAULT_LOCKED_PLAYER_SLOTS_FILE_NAME;
        }

        stop(shouldStop, configName);
    }
}