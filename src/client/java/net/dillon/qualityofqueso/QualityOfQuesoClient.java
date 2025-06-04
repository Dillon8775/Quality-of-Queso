package net.dillon.qualityofqueso;

import net.dillon.qualityofqueso.option.ModOptions;
import net.fabricmc.api.ClientModInitializer;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class QualityOfQuesoClient implements ClientModInitializer {
	public static final List<Integer> keys = List.of(GLFW.GLFW_KEY_T, GLFW.GLFW_KEY_E);
	public static final List<Integer> disallowedKeys = List.of(
			GLFW.GLFW_KEY_ESCAPE,
			GLFW.GLFW_KEY_LEFT_SHIFT,
			GLFW.GLFW_KEY_RIGHT_SHIFT,
			GLFW.GLFW_KEY_LEFT_CONTROL,
			GLFW.GLFW_KEY_RIGHT_CONTROL,
			GLFW.GLFW_KEY_LEFT_ALT,
			GLFW.GLFW_KEY_RIGHT_ALT,
			GLFW.GLFW_KEY_LEFT_SUPER,
			GLFW.GLFW_KEY_RIGHT_SUPER
	);

	@Override
	public void onInitializeClient() {
		ModOptions.loadConfig();
		QualityOfQueso.info("Quality of Queso has successfully loaded!");
	}

	/**
	 * Returns the options.
	 */
	public static ModOptions options() {
		return ModOptions.OPTIONS;
	}
}