package net.dillon.qualityofqueso.platform;

import net.blay09.mods.balm.Balm;
import net.dillon.qualityofqueso.util.MixinPluginUtil;
import net.dillon.qualityofqueso.util.VersionType;

import java.nio.file.Path;

public interface PlatformHelper {

    /**
     * @return the mod version.
     */
    default String getModVersion() {
        return "1.7.2";
    }

    /**
     * @return the version type.
     */
    default VersionType getVersionType() {
        return VersionType.PATCH;
    }

    /**
     * @return if the platform is on NeoForged.
     */
    default boolean isNeoForged() {
        return Balm.platform().name().equals("neoforge");
    }

    /**
     * Gets the config directory for the supported platform.
     */
    Path getConfigDir();

    /**
     * @return if a mod is loaded on a specific platform. Used only in {@link MixinPluginUtil}.
     */
    boolean isModLoaded(String modId);
}