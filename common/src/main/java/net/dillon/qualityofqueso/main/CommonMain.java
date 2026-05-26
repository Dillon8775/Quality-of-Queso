package net.dillon.qualityofqueso.main;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.core.BalmRegistrars;
import net.dillon.qualityofqueso.option.ModCommonOptions;
import net.dillon.qualityofqueso.option.UniversalOptions;
import net.dillon.qualityofqueso.platform.MultiLoader;
import net.dillon.qualityofqueso.platform.ReleaseType;
import net.dillon.qualityofqueso.util.ModConstants;

import static net.dillon.qualityofqueso.helper.ModHelper.*;
import static net.dillon.qualityofqueso.main.CommonEvents.registerCommonPackets;

/**
 * The main entrypoint for Quality of Queso.
 */
public class CommonMain {

    /**
     * Initializes common (or server)-side features.
     */
    public static void initialize(BalmRegistrars balmRegistrars) {
        registerCommonPackets();

        checkCommonConfigs();
        loadCommonConfigs();
        info("Quality of Queso version " + MultiLoader.getPlatform().getModVersion() + " (for " + Balm.platform().name() + ") loaded successfully!");

        if (MultiLoader.getPlatform().getReleaseType() == ReleaseType.BETA) {
            warn("Please be aware that this version of Quality of Queso is marked as beta, use at your own risk!");
        } else if (MultiLoader.getPlatform().getReleaseType() == ReleaseType.ALPHA) {
            error("Please note that this version of Quality of Queso is in the alpha state, so there will be unknown bugs. Use at your own risk!");
        }
    }

    /**
     * Loads all common configs.
     */
    private static void loadCommonConfigs() {
        UniversalOptions.INSTANCE.load();
        ModCommonOptions.INSTANCE.load();
    }

    /**
     * Ensures common (or server-side) configs are valid configs, and handles invalid configs if necessary.
     */
    private static void checkCommonConfigs() {
        boolean shouldStop = false;
        String configName = "";
        if (UniversalOptions.INSTANCE.getInstance() == null) {
            error("Quality of Queso's universal config is null! Please delete it and relaunch your game.");
            shouldStop = true;
            configName = "universal config";
        }
        if (ModCommonOptions.INSTANCE.getInstance() == null) {
            error("Quality of Queso's common config is null! Please delete it and relaunch your game.");
            shouldStop = true;
            configName = ModConstants.DEFAULT_COMMON_CONFIG_FILE_NAME;
        }

        stop(shouldStop, configName);
    }
}