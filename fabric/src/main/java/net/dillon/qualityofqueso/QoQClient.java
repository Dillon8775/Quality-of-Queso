package net.dillon.qualityofqueso;

import net.dillon.qualityofqueso.debug.ModHudEntries;
import net.dillon.qualityofqueso.event.ClientEvents;
import net.dillon.qualityofqueso.sound.ModSoundEvents;
import net.dillon.qualityofqueso.util.ModUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * The main, client-side entrypoint for {@code QoQ.}
 */
@Environment(EnvType.CLIENT)
public class QoQClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModHudEntries.registerDebugEntries();

        ClientEvents.registerClientEvents();
        ClientEvents.registerCommands();
        ClientEvents.registerKeybindings();

        ModSoundEvents.initializeSoundEvents();

        ModUtil.info("Quality of Queso has successfully loaded!");
    }
}