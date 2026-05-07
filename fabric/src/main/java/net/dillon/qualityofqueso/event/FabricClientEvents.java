package net.dillon.qualityofqueso.event;

import net.dillon.qualityofqueso.keybind.ModKeyMappings;
import net.dillon.qualityofqueso.main.ClientEvents;
import net.dillon.qualityofqueso.packet.GlowSearchC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class FabricClientEvents {

    /**
     * Registers all {@code keybindings.}
     */
    public static void registerFabricKeyBindings() {
        ModKeyMappings.initKeybinds();
    }

    /**
     * Registers all {@code client-side events.}
     */
    public static void registerFabricClientEvents() {
        ClientPlayConnectionEvents.JOIN.register((handler, packet, minecraft) -> {
            ClientEvents.onPlayerJoin(minecraft);

            if (!ClientPlayNetworking.canSend(GlowSearchC2SPacket.PACKET_TYPE)) {
                ClientEvents.warnModNotPresent(minecraft);
            }
        });

        ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((level, minecraft) -> {
            ClientEvents.afterLevelChangeOrRespawn();
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, minecraft) -> {
            ClientEvents.onPlayerDisconnect(minecraft);
        });
    }
}