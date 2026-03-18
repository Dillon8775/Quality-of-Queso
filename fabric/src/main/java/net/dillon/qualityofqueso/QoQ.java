package net.dillon.qualityofqueso;

import net.dillon.qualityofqueso.event.CommonEvents;
import net.dillon.qualityofqueso.util.ModUtil;
import net.fabricmc.api.ModInitializer;

/**
 * The server-side (or main) entrypoint for {@code QoQ.}
 */
public class QoQ implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonEvents.registerPayloads();
        CommonEvents.registerCommands();

        ModUtil.initializeSuccess();
    }
}