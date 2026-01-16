package net.dillon.qualityofqueso.util;

import net.dillon.qualityofqueso.main.QoQ;
import net.dillon.qualityofqueso.option.BaseOptions;
import net.dillon.qualityofqueso.option.CommonOptions;
import net.dillon.qualityofqueso.option.ModClientOptions;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;

import static net.dillon.qualityofqueso.main.QoQ.options;

@Mod.EventBusSubscriber(modid = QoQ.MOD_ID, value = Dist.CLIENT)
public class ServerConfigPreset {

    @SubscribeEvent
    public static void onClientJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        if (!options().multiServerConfigs) {
            return;
        }

        Minecraft instance = Minecraft.getInstance();
        if (instance.getCurrentServer() == null) {
            return;
        } else if (instance.getCurrentServer().ip == null) {
            return;
        }

        String address = instance.getCurrentServer().name;
        String safe = address.replace(":", "_").replace(".", "-");

        File serverDir = FMLPaths.CONFIGDIR.get()
                .resolve("qoq/server-configs")
                .toFile();

        String clientServerConfig = safe + "_client.json";
        String commonServerConfig = safe + "_common.json";
        File clientServerFile = new File(serverDir, clientServerConfig);
        File commonServerFile = new File(serverDir, commonServerConfig);

        ModClientOptions cachedClientInstance = ModClientOptions.CLIENT_OPTIONS.getInstance();
        CommonOptions cachedCommonInstance = CommonOptions.COMMON_OPTIONS.getInstance();
        final boolean configExists = clientServerFile.exists() && commonServerFile.exists();
        ModClientOptions.CLIENT_OPTIONS.setCustomDirectory(serverDir);
        ModClientOptions.CLIENT_OPTIONS.setFileName(clientServerConfig);
        CommonOptions.COMMON_OPTIONS.setCustomDirectory(serverDir);
        CommonOptions.COMMON_OPTIONS.setFileName(commonServerConfig);
        if (!configExists) {
            ModClientOptions.CLIENT_OPTIONS.setInstance(cachedClientInstance);
            ModClientOptions.CLIENT_OPTIONS.save();
            CommonOptions.COMMON_OPTIONS.setInstance(cachedCommonInstance);
            CommonOptions.COMMON_OPTIONS.save();
            ModUtil.info("Creating new QoQ server config instance... (" + address + ")");
        }
        ModClientOptions.CLIENT_OPTIONS.load();
        CommonOptions.COMMON_OPTIONS.load();

        String message = !configExists ? "qualityofqueso.created_server_config" : "qualityofqueso.loaded_server_config";
        if (instance.player != null) {
            instance.player.displayClientMessage(Component.translatable(message).withStyle(ChatFormatting.GOLD), false);
        }
        ModUtil.info("Loaded QoQ config for " + address + ".");
    }

    @SubscribeEvent
    public static void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        ModClientOptions.CLIENT_OPTIONS.clearCustomDirectory();
        ModClientOptions.CLIENT_OPTIONS.setFileName(BaseOptions.DEFAULT_CLIENT_FILE_NAME);
        ModClientOptions.CLIENT_OPTIONS.load();
        CommonOptions.COMMON_OPTIONS.clearCustomDirectory();
        CommonOptions.COMMON_OPTIONS.setFileName(BaseOptions.DEFAULT_COMMON_FILE_NAME);
        CommonOptions.COMMON_OPTIONS.load();
        if (options().multiServerConfigs) {
            ModUtil.info("Reverting back to global QoQ config.");
        }
    }
}
