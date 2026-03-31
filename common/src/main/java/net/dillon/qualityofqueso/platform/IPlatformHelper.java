package net.dillon.qualityofqueso.platform;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.nio.file.Path;

public interface IPlatformHelper {

    /**
     * @return the mod version.
     */
    default String getModVersion() {
        return "1.6.4";
    }

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
     * @return if the payload can send over to the server.
     */
    boolean isSafeToSend(CustomPacketPayload packet);

    /**
     * Gets the config directory for the supported platform.
     */
    Path getConfigDir();

    /**
     * Sends a packet to the server.
     */
    void sendToServer(CustomPacketPayload payload);
}