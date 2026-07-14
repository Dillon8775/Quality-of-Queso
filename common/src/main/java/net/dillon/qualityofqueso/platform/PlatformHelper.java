package net.dillon.qualityofqueso.platform;

import net.dillon.qualityofqueso.util.MixinPluginUtil;
import net.minecraft.client.player.LocalPlayer;

import java.nio.file.Path;

public interface PlatformHelper {

    /**
     * @return the version type.
     */
    default VersionType getVersionType() {
        return VersionType.PATCH_2;
    }

    /**
     * @return the release type.
     */
    default PlatformRelease getReleaseType() {
        return PlatformRelease.BETA;
    }

    /**
     * @return the mod version.
     */
    String getModVersion();

    /**
     * @return the platform name.
     */
    PlatformName getPlatformName();

    /**
     * Gets the config directory for the supported platform.
     */
    Path getConfigDir();

    /**
     * @return if a mod is loaded on a specific platform. Used only in {@link MixinPluginUtil}.
     */
    boolean isModLoaded(PlatformMod mod);

    /**
     * @return if a {@code Quality of Queso} packet can be sent.
     */
    boolean canSendPacket(LocalPlayer localPlayer);

    /**
     * Adds all mod ids to a list, for the ItemArgument.
     */
    void addModIds();
}