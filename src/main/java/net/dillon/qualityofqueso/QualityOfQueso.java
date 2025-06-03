package net.dillon.qualityofqueso;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QualityOfQueso implements ModInitializer {
	public static final String MOD_ID = "qualityofqueso";
	private static final Logger LOGGER = LoggerFactory.getLogger("Quality of QUESO");

	@Override
	public void onInitialize() {
	}

	/**
	 * Sends a message to console.
	 */
	public static void info(String message) {
		LOGGER.info(message);
	}
}