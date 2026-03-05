package net.dillon.qualityofqueso;

import net.dillon.qualityofqueso.event.CommonEvents;
import net.fabricmc.api.ModInitializer;

/**
 * The server-side (or main) entrypoint for {@code QoQ.}
 */
public class QoQ implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonEvents.registerPayloads();
    }
}