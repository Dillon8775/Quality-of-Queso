package net.dillon.qualityofqueso;

import net.fabricmc.api.ClientModInitializer;

public class QualityOfQuesoClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		QuesoOptions.loadConfig();
	}
}