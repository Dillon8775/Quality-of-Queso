package net.dillon.qualityofqueso.event;

import net.dillon.qualityofqueso.util.ModUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;

import static net.dillon.qualityofqueso.util.ModUtil.*;

/**
 * Client events for Quality of Queso.
 */
public class ClientEvents {

    public static void onPlayerJoin(Minecraft minecraft) {
        if (uoptions().main.multiServerConfigs) {
            ModUtil.LOADED = true;
            loadServerConfig();
        }
        if (isOnServer(minecraft) && options().misc.alwaysPreventRageQuitting) {
            options().misc.preventRageQuitting = true;
            saveAll(minecraft);
        }
        sendClientOptionsToServer();
    }

    public static void afterLevelChangeOrRespawn() {
        resetArmorHudState();
    }

    public static void onPlayerDisconnect(Minecraft minecraft) {
        resetArmorHudState();

        if (isOnServer(minecraft)) {
            unloadServerConfig();
        }
    }
}