package net.dillon.qualityofqueso.util;

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
     * Sends a {@code debug message} to the console.
     */
    public static void debug(String message) {
        LOGGER.debug(message);
    }
}