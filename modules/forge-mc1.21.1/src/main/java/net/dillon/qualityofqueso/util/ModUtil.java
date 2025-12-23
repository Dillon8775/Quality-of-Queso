package net.dillon.qualityofqueso.util;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for the Quality of Queso mod.
 */
public class ModUtil {
     private static final Logger LOGGER = LoggerFactory.getLogger("Quality of Queso");

    /**
     * Sends a message to console.
     */
    public static void info(String message) {
        LOGGER.info(message);
    }

    /**
     * @return if the player is on a server.
     */
    @OnlyIn(Dist.CLIENT)
    public static boolean isOnServer(Minecraft client) {
        return !client.isSingleplayer() && !(client.getCurrentServer() == null);
    }
}