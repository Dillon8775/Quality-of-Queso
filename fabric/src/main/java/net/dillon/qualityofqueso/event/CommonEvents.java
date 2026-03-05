package net.dillon.qualityofqueso.event;

import net.dillon.qualityofqueso.packet.GlowSearchC2SPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import static net.dillon.qualityofqueso.util.ModUtil.handleGlowPayload;

public class CommonEvents {

    /**
     * Registers all {@code QoQ} payloads.
     */
    public static void registerPayloads() {
        PayloadTypeRegistry.serverboundPlay().register(
                GlowSearchC2SPayload.PAYLOAD_ID,
                GlowSearchC2SPayload.CODEC
        );

        ServerPlayNetworking.registerGlobalReceiver(
                GlowSearchC2SPayload.PAYLOAD_ID,
                (payload, context) -> {
                    handleGlowPayload(payload, context.player());
                }
        );
    }
}