package net.dillon.qualityofqueso.packet;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Packet handling.
 */
public class ServerHandler {
    private static int packetId = 0;
    private static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
            .named(ResourceLocation.parse("qualityofqueso:main"))
            .networkProtocolVersion(() -> "1")
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .simpleChannel();

    private static int id() {
        return packetId++;
    }

    /**
     * Registers the glow to search payload.
     */
    public static void registerGlowSearchPayload() {
        INSTANCE.messageBuilder(GlowSearchC2SPayload.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(GlowSearchC2SPayload::encode)
                .decoder(GlowSearchC2SPayload::new)
                .consumerMainThread(GlowSearchC2SPayload::handle)
                .add();
    }

    /**
     * Sends the payload over to the server.
     */
    public static void sendToServer(Object msg) {
        INSTANCE.send(PacketDistributor.SERVER.noArg(), msg);
    }
}