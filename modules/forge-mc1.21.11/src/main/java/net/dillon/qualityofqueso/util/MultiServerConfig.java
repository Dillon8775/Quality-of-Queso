package net.dillon.qualityofqueso.util;

import net.dillon.qualityofqueso.main.QoQ;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.dillon.qualityofqueso.main.QoQ.*;

@Mod.EventBusSubscriber(modid = QoQ.MOD_ID, value = Dist.CLIENT)
public class MultiServerConfig {

    // Load multi-server config
    @SubscribeEvent
    public static void onClientJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        if (uoptions().multiServerConfigs) {
            QoQ.LOADED = true;
            loadServerConfig();
        }
        if (isOnServer(Minecraft.getInstance()) && options().alwaysPreventRageQuitting) {
            options().preventRageQuitting = true;
            saveAll(Minecraft.getInstance());
        }
    }

    // Unload multi-server config
    @SubscribeEvent
    public static void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        if (isOnServer(Minecraft.getInstance())) {
            unloadServerConfig();
        }
    }
}