package net.dillon.qualityofqueso.main;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.core.BalmRegistrars;
import net.dillon.dillonlib.annotation.Dill;
import net.dillon.dillonlib.annotation.DillType;
import net.dillon.dillonlib.platform.info.PlatformRelease;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.option.ModCommonOptions;
import net.dillon.qualityofqueso.option.UniversalOptions;
import net.dillon.qualityofqueso.platform.QualityOfQuesoPlatforms;

import java.util.Set;
import java.util.TreeSet;

import static net.dillon.qualityofqueso.helper.ModHelper.stop;
import static net.dillon.qualityofqueso.main.CommonEvents.registerCommonPackets;

/**
 * The main entrypoint for Quality of Queso.
 */
@Dill(DillType.COMMON)
public class CommonMain {
    public static Set<String> MOD_IDS = new TreeSet<>();

    /**
     * Initializes common (or server)-side features.
     */
    public static void initialize(BalmRegistrars balmRegistrars) {
        registerCommonPackets();

        checkCommonConfigs();
        loadCommonConfigs();

        QualityOfQuesoPlatforms.getPlatform().addModIds();

        ModConstants.LOGGER.info("Quality of Queso version {} (for {}) loaded successfully!", QualityOfQuesoPlatforms.getPlatform().modVersion(), Balm.platform().name());

        if (QualityOfQuesoPlatforms.getPlatform().platformRelease() == PlatformRelease.BETA) {
            ModConstants.LOGGER.warn("Please be aware that this version of Quality of Queso is marked as beta, use at your own risk!");
        } else if (QualityOfQuesoPlatforms.getPlatform().platformRelease() == PlatformRelease.ALPHA) {
            ModConstants.LOGGER.error("Please note that this version of Quality of Queso is in the alpha state, so there will be unknown bugs. Use at your own risk!");
        }

        if (ModConstants.HAS_UPDATE) {
            ModConstants.LOGGER.warn("A newer version of Quality of Queso is available. Download it here: https://modrinth.com/mod/quality-of-queso/versions");
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
            ModConstants.LOGGER.error("Quality of Queso's universal config is null! Please delete it and relaunch your game.");
            shouldStop = true;
            configName = "universal config";
        }
        if (ModCommonOptions.INSTANCE.getInstance() == null) {
            ModConstants.LOGGER.error("Quality of Queso's common config is null! Please delete it and relaunch your game.");
            shouldStop = true;
            configName = ModConstants.DEFAULT_COMMON_CONFIG_FILE_NAME;
        }

        stop(shouldStop, configName);
    }
}