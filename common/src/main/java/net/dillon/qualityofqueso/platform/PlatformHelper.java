package net.dillon.qualityofqueso.platform;

import net.blay09.mods.balm.api.Balm;
import net.dillon.qualityofqueso.util.MixinPluginUtil;
import net.minecraft.client.player.LocalPlayer;

import java.nio.file.Path;

public interface PlatformHelper {

    /**
     * @return the mod version.
     */
    String getModVersion();

    /**
     * @return the version type.
     */
    default VersionType getVersionType() {
        return VersionType.PATCH_2;
    }

    /**
     * @return the release type.
     */
    default ReleaseType getReleaseType() {
        return ReleaseType.BETA;
    }

    /**
     * @return if the platform is on NeoForged.
     */
    default boolean isNeoForged() {
        return Balm.getPlatform().equals("neoforge");
    }

    /**
     * @return if {@code YACL} is loaded.
     */
    default boolean isYaclLoaded() {
        return isModLoaded("yet_another_config_lib_v3");
    }

    /**
     * Gets the config directory for the supported platform.
     */
    Path getConfigDir();

    /**
     * @return if a mod is loaded on a specific platform. Used only in {@link MixinPluginUtil}.
     */
    boolean isModLoaded(String modId);

    /**
     * @return if a {@code Quality of Queso} packet can be sent.
     */
    boolean canSendPacket(LocalPlayer localPlayer);

    /**
     * Adds all mod ids to a list, for the ItemArgument.
     */
    void addModIds();
}