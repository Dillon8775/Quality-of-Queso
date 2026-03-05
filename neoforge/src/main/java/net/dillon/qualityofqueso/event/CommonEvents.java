package net.dillon.qualityofqueso.event;

import net.dillon.qualityofqueso.QoQ;
import net.dillon.qualityofqueso.packet.GlowSearchC2SPayload;
import net.dillon.qualityofqueso.util.ModUtil;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = QoQ.MOD_ID)
public class CommonEvents {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                GlowSearchC2SPayload.PAYLOAD_ID,
                GlowSearchC2SPayload.CODEC,
                CommonEvents::handle
        );
    }

    private static void handle(GlowSearchC2SPayload payload, IPayloadContext context) {
        ModUtil.handleGlowPayload(payload, (ServerPlayer)context.player());
    }
}