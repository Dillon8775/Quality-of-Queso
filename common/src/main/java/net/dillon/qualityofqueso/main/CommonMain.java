package net.dillon.qualityofqueso.main;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.core.BalmRegistrars;
import net.dillon.dillonlib.annotation.Dill;
import net.dillon.dillonlib.annotation.DillEnv;
import net.dillon.dillonlib.platform.info.Release;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.option.ModCommonOptions;
import net.dillon.qualityofqueso.platform.QualityOfQuesoPlatforms;
import net.dillon.qualityofqueso.sound.ModSoundEvents;

import java.util.Set;
import java.util.TreeSet;

import static net.dillon.qualityofqueso.main.CommonEvents.registerPackets;

/**
 * The main entrypoint for Quality of Queso.
 */
@Dill(env = DillEnv.COMMON)
public class CommonMain {
    public static Set<String> MOD_IDS = new TreeSet<>();

    /**
     * Initializes common (or server)-side features.
     */
    public static void initialize(BalmRegistrars registrars) {
        ModSoundEvents.registerCommonSounds(registrars);

        registerPackets();
        loadCommonConfigs();

        QualityOfQuesoPlatforms.getPlatformHelper().addModIds();

        ModConstants.LOGGER.info("Quality of Queso version {} (for {}) loaded successfully!", QualityOfQuesoPlatforms.getPlatform().modVersion(), Balm.platform().name());

        if (QualityOfQuesoPlatforms.getPlatform().release() == Release.BETA) {
            ModConstants.LOGGER.warn("Please be aware that this version of Quality of Queso is marked as beta, use at your own risk!");
        } else if (QualityOfQuesoPlatforms.getPlatform().release() == Release.ALPHA) {
            ModConstants.LOGGER.error("Please note that this version of Quality of Queso is in the alpha state, so there will be unknown bugs. Use at your own risk!");
        }

        if (ModConstants.MOD_INFO.hasUpdate()) {
            ModConstants.LOGGER.warn("A newer version of Quality of Queso is available. Download it here: https://modrinth.com/mod/quality-of-queso/versions");
        }
    }

    /**
     * Loads all common configs.
     */
    private static void loadCommonConfigs() {
        ModCommonOptions.INSTANCE.load();
    }
}