package net.dillon.qualityofqueso.platform;

import net.blay09.mods.balm.Balm;

import java.nio.file.Path;

public interface PlatformHelper {

    /**
     * @return the mod version.
     */
    default String getModVersion() {
        return "1.6.4";
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
}