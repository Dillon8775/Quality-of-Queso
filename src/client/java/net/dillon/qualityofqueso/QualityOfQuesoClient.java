package net.dillon.qualityofqueso;

import net.dillon.qualityofqueso.option.ModOptions;
import net.fabricmc.api.ClientModInitializer;

public class QualityOfQuesoClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ModOptions.loadConfig();
		QualityOfQueso.info("Quality of Queso has successfully loaded!");
	}
}