package net.dillon.qualityofqueso.main;

import net.dillon.dillonlib.task.CommonTasks;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.helper.ModHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

import static net.dillon.dillonlib.task.ClientTasks.executeIfClientPlayer;
import static net.dillon.qualityofqueso.helper.ModHelper.*;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.universal;

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
        if (universal().multiServerConfigs) {
            ModHelper.LOADED = true;
            loadServerConfig();
        }
        if (isOnServer(minecraft) && client().misc().forceAntiRageQuit) {
            client().misc().antiRageQuit = true;
            saveAndApplyConfigs(minecraft);
        }
        executeIfClientPlayer(localPlayer -> {
            if (client().accessibility().serverWarnings && isOnServer(minecraft)) {
                for (String server : bannedServers) {
                    if (minecraft.getCurrentServer().ip.contains(server)) {
                        localPlayer.sendSystemMessage(Component.translatable("qualityofqueso.gui.banned_server",
                                Component.literal(minecraft.getCurrentServer().ip).withStyle(ChatFormatting.DARK_RED)));
                    }
                }
                if (!universal().multiServerConfigs) {
                    localPlayer.sendSystemMessage(Component.translatable("qualityofqueso.gui.enable_multi_server_configs"));
                }
            }
            if (ModConstants.HAS_UPDATE) {
                CommonTasks.sendUpdateMessage(localPlayer,
                        Component.translatable("qualityofqueso.title").withStyle(ChatFormatting.GOLD),
                        "https://modrinth.com/mod/quality-of-queso/versions",
                        TextColor.GOLD.getValue());
            }
        });
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

        if (client().accessibility().serverWarnings && minecraft.player != null) {
            minecraft.player.sendSystemMessage(Component.translatable("qualityofqueso.gui.mod_not_installed"));
        }
    }
}