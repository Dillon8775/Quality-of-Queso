package net.dillon.qualityofqueso.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * The payload (or packet) for taking in the data required to make item frames glow.
 */
public record GlowSearchC2SPacket(String query, boolean matchCase, boolean clear, int timer,
                                  int radius) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("qualityofqueso", "glow_search");
    public static final Type<GlowSearchC2SPacket> PACKET_TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, GlowSearchC2SPacket> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    GlowSearchC2SPacket::query,
                    ByteBufCodecs.BOOL,
                    GlowSearchC2SPacket::matchCase,
                    ByteBufCodecs.BOOL,
                    GlowSearchC2SPacket::clear,
                    ByteBufCodecs.INT,
                    GlowSearchC2SPacket::timer,
                    ByteBufCodecs.INT,
                    GlowSearchC2SPacket::radius,
                    GlowSearchC2SPacket::new
            );

    /**
     * Returns the {@code id} for the Glow Search payload.
     */
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}