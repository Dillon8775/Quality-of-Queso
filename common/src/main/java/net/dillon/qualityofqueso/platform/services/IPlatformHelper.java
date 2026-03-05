package net.dillon.qualityofqueso.platform.services;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.io.File;
import java.nio.file.Path;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the config directory for the supported platform.
     */
    Path getConfigDir();

    /**
     * Sends a packet to the server.
     */
    void sendToServer(CustomPacketPayload payload);

    default String getEnvironmentName() {
        return this.isDevelopmentEnvironment() ? "development" : "production";
    }
}