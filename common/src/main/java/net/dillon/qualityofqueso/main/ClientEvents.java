package net.dillon.qualityofqueso.main;

import net.dillon.dillonlib.task.CommonTasks;
import net.dillon.qualityofqueso.option.ModClientOptions;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

import static net.dillon.dillonlib.task.ClientTasks.executeIfClientPlayer;
import static net.dillon.qualityofqueso.helper.ModConstants.MOD_INFO;
import static net.dillon.qualityofqueso.helper.ModHelper.*;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Client events for Quality of Queso.
 */
public class ClientEvents {

    public static void afterLevelChangeOrRespawn() {
        resetArmorHudState();
    }

    public static void onPlayerJoin(Minecraft minecraft) {
        if (isOnServer() && client().misc().forceAntiRageQuit) {
            client().misc().antiRageQuit = true;
            saveAndApplyChanges();
        }

        executeIfClientPlayer(localPlayer -> {
            if (client().accessibility().serverWarnings && isOnServer()) {
                for (String server : client().accessibility().bannedServers) {
                    if (minecraft.getCurrentServer().ip.contains(server)) {
                        localPlayer.sendSystemMessage(Component.translatable("qualityofqueso.gui.banned_server",
                                Component.literal(minecraft.getCurrentServer().ip).withStyle(ChatFormatting.DARK_RED)));
                    }
                }
            }

            CommonTasks.sendUpdateMessage(MOD_INFO, localPlayer, TextColor.GOLD.getValue());
        });

        sendClientPreferencesToServer();
    }

    public static void onPlayerDisconnect(Minecraft minecraft) {
        resetArmorHudState();

        ModClientOptions.INSTANCE.save();
    }

    /**
     * Warns the user that certain features won't be available because the server doesn't have the mod installed.
     */
    public static void warnModNotPresent(Minecraft minecraft) {
        if (!modEnabled()) {
            return;
        }

        if (client().accessibility().serverWarnings && minecraft.player != null) {
            minecraft.player.sendSystemMessage(Component.translatable("qualityofqueso.gui.mod_not_installed"));
        }
    }
}