package net.dillon.qualityofqueso.event;

import net.dillon.qualityofqueso.keybind.ModKeyMappings;
import net.dillon.qualityofqueso.main.ClientEvents;
import net.dillon.qualityofqueso.packet.GlowSearchC2SPacket;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

@EventBusSubscriber(modid = ModConstants.MOD_ID, value = Dist.CLIENT)
public class NeoForgeClientEvents {

    @SubscribeEvent
    public static void registerKeybindings(RegisterKeyMappingsEvent event) {
        ModKeyMappings.initKeybinds();
    }

    @SubscribeEvent
    public static void onClientJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientEvents.onPlayerJoin(minecraft);

        if (!NetworkRegistry.hasChannel(event.getPlayer().connection, GlowSearchC2SPacket.ID)) {
            minecraft.execute(() -> ClientEvents.warnModNotPresent(minecraft));
        }
    }

    @SubscribeEvent
    public static void onLevelChange(LevelEvent.Load event) {
        ClientEvents.afterLevelChangeOrRespawn();
    }

    @SubscribeEvent
    public static void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientEvents.onPlayerDisconnect(Minecraft.getInstance());
    }
}