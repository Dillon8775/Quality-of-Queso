package net.dillon.qualityofqueso.event;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.client.ConnectedToServerEvent;
import net.blay09.mods.balm.api.event.client.DisconnectedFromServerEvent;
import net.blay09.mods.balm.api.event.client.screen.ScreenInitEvent;
import net.dillon.qualityofqueso.util.ModUtil;
import net.minecraft.client.gui.screens.DeathScreen;

import static net.dillon.qualityofqueso.util.ModUtil.*;

/**
 * Client events for Quality of Queso.
 */
public class ClientEvents {

    public static void registerClientEvents() {
        Balm.getEvents().onEvent(ConnectedToServerEvent.class, event -> {
            if (uoptions().main.multiServerConfigs) {
                ModUtil.LOADED = true;
                loadServerConfig();
            }
            if (isOnServer(event.getClient()) && options().misc.alwaysPreventRageQuitting) {
                options().misc.preventRageQuitting = true;
                saveAll(event.getClient());
            }
            sendClientOptionsToServer();
        });

        Balm.getEvents().onEvent(DisconnectedFromServerEvent.class, event -> {
            resetArmorHudState();

            if (isOnServer(event.getClient())) {
                unloadServerConfig();
            }
        });

        Balm.getEvents().onEvent(ScreenInitEvent.class, event -> {
            if (event.getScreen() instanceof DeathScreen) {
                resetArmorHudState();
            }
        });
    }
}