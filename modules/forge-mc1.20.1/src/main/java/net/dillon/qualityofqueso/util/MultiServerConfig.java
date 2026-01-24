package net.dillon.qualityofqueso.util;

import net.dillon.qualityofqueso.main.QoQ;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.dillon.qualityofqueso.main.QoQ.loadServerConfig;
import static net.dillon.qualityofqueso.main.QoQ.unloadServerConfig;

@Mod.EventBusSubscriber(modid = QoQ.MOD_ID, value = Dist.CLIENT)
public class MultiServerConfig {

    // Load multi-server config
    @SubscribeEvent
    public static void onClientJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        loadServerConfig();
    }

    // Unload multi-server config
    @SubscribeEvent
    public static void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        unloadServerConfig();
    }
}
