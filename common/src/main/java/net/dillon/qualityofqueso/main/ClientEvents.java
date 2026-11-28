package net.dillon.qualityofqueso.main;

import net.dillon.dillonlib.task.CommonTasks;
import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.util.CustomItemGroup;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.*;

import static net.dillon.dillonlib.task.ClientTasks.executeIfClientPlayer;
import static net.dillon.dillonlib.task.ClientTasks.getCurrentServerIp;
import static net.dillon.qualityofqueso.helper.ModConstants.MOD_INFO;
import static net.dillon.qualityofqueso.helper.ModHelper.*;
import static net.dillon.qualityofqueso.option.ClientOptionInstances.client;

/**
 * Client events for Quality of Queso.
 */
public class ClientEvents {

    public static void afterLevelChangeOrRespawn() {
        resetArmorHudState();
    }

    public static void onPlayerJoin(Minecraft minecraft) {
        if (isOnServer() && client().gui().forceAntiRageQuit) {
            client().gui().antiRageQuit = true;
            saveAndApplyChanges();
        }

        executeIfClientPlayer(localPlayer -> {
            if (client().misc().serverWarnings && isOnServer()) {
                for (String server : client().misc().bannedServers) {
                    String serverIp = getCurrentServerIp(false);

                    if (serverIp.contains(server)) {
                        localPlayer.sendSystemMessage(Component.translatable("qualityofqueso.gui.banned_server",
                                Component.literal(serverIp).withStyle(ChatFormatting.DARK_RED)));
                    }
                }
            }

            if (CustomItemGroup.ERROR_PARSING_FILE) {
                localPlayer.sendSystemMessage(Component.translatable("qualityofqueso.custom_item_group_file_parse_failed")
                        .withStyle(ChatFormatting.RED)
                        .withStyle(
                                Style.EMPTY
                                        .withHoverEvent(
                                                new HoverEvent.ShowText(Component.literal(CustomItemGroup.PARSING_ERROR))
                                        )
                                        .withClickEvent(
                                                new ClickEvent.OpenFile(CustomItemGroup.filePath())
                                        )
                        )
                );
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

        if (client().misc().serverWarnings && minecraft.player != null) {
            minecraft.player.sendSystemMessage(Component.translatable("qualityofqueso.gui.mod_not_installed"));
        }
    }
}