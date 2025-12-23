package net.dillon.qualityofqueso.packet;

import net.minecraft.resources.Identifier;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

/**
 * Packet handling.
 */
public class ServerHandler {
    private static final SimpleChannel INSTANCE = ChannelBuilder
            .named(Identifier.parse("qualityofqueso:main"))
            .networkProtocolVersion(1)
            .clientAcceptedVersions((status, s) -> true)
            .serverAcceptedVersions((status, a) -> true)
            .simpleChannel();

    /**
     * Registers the glow to search payload.
     */
    public static void registerGlowSearchPayload() {
        INSTANCE.messageBuilder(GlowSearchC2SPayload.class, NetworkDirection.PLAY_TO_SERVER)
                .encoder(GlowSearchC2SPayload::encode)
                .decoder(GlowSearchC2SPayload::new)
                .consumerMainThread(GlowSearchC2SPayload::handle)
                .add();
    }

    /**
     * Sends the payload over to the server.
     */
    public static void sendToServer(Object msg) {
        INSTANCE.send(msg, PacketDistributor.SERVER.noArg());
    }
}