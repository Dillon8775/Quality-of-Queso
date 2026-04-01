package net.dillon.qualityofqueso.main;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.core.BalmRegistrars;
import net.dillon.qualityofqueso.option.base.BaseOptions;
import net.dillon.qualityofqueso.option.instance.ModCommonOptions;
import net.dillon.qualityofqueso.option.instance.UniversalOptions;
import net.dillon.qualityofqueso.platform.MultiLoader;

import static net.dillon.qualityofqueso.event.CommonEvents.registerPackets;
import static net.dillon.qualityofqueso.util.ModUtil.*;

/**
 * The main entrypoint for Quality of Queso.
 */
public class Main {

    public static void initialize(BalmRegistrars balmRegistrars) {
        registerPackets();

        checkCommonConfigs();
        info("Quality of Queso version " + MultiLoader.getPlatform().getModVersion() + " (for " + Balm.platform().name() + ") loaded successfully!");
    }

    private static void checkCommonConfigs() {
        boolean shouldStop = false;
        String configName = "";
        if (UniversalOptions.UNIVERSAL.getInstance() == null) {
            error("Quality of Queso's universal config is null! Please delete it and relaunch your game.");
            shouldStop = true;
            configName = "universal config";
        }
        if (ModCommonOptions.COMMON.getInstance() == null) {
            error("Quality of Queso's common config is null! Please delete it and relaunch your game.");
            shouldStop = true;
            configName = BaseOptions.DEFAULT_COMMON_FILE_NAME;
        }

        stop(shouldStop, configName);
    }
}