package net.dillon.qualityofqueso.main;

import net.dillon.qualityofqueso.helper.ModHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.helper.ModHelper.*;

/**
 * Client events for Quality of Queso.
 */
public class ClientEvents {
    private static final String[] bannedServers = new String[]{
            "hypixel"
    };

    public static void afterLevelChangeOrRespawn() {
        resetArmorHudState();
    }

    public static void onPlayerJoin(Minecraft minecraft) {
        if (universalOptionsInstance().multiServerConfigs) {
            ModHelper.LOADED = true;
            loadServerConfig();
        }
        if (isOnServer(minecraft) && clientOptionsInstance().getMiscOptions().forceAntiRageQuit) {
            clientOptionsInstance().getMiscOptions().antiRageQuit = true;
            saveAndApplyConfigs(minecraft);
        }
        if (isOnServer(minecraft) && minecraft.player != null) {
            for (String server : bannedServers) {
                if (minecraft.getCurrentServer().ip.contains(server)) {
                    minecraft.player.sendSystemMessage(Component.translatable("qualityofqueso.gui.banned_server",
                            Component.literal(minecraft.getCurrentServer().ip).withStyle(ChatFormatting.DARK_RED)));
                }
            }
            if (!universalOptionsInstance().multiServerConfigs) {
                minecraft.player.sendSystemMessage(Component.translatable("qualityofqueso.gui.enable_multi_server_configs"));
            }
        }
        sendClientPreferencesToServer();
    }

    public static void onPlayerDisconnect(Minecraft minecraft) {
        resetArmorHudState();

        if (isOnServer(minecraft)) {
            unloadServerConfig();
        }
    }

    /**
     * Warns the user that certain features won't be available because the server doesn't have the mod installed.
     */
    public static void warnModNotPresent(Minecraft minecraft) {
        if (!modEnabled(minecraft)) {
            return;
        }

        if (minecraft.player != null) {
            minecraft.player.sendSystemMessage(Component.translatable("qualityofqueso.gui.mod_not_installed"));
        }
    }
}