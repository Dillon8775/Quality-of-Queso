package net.dillon.qualityofqueso.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * The payload (or packet) for taking in the data required to make item frames glow.
 */
public record GlowSearchC2SPayload(String query, boolean matchCase, boolean clear, int timer, int radius) implements CustomPacketPayload {
    public static final Identifier ID = Identifier.parse("qualityofqueso:glow_search");
    public static final Type<GlowSearchC2SPayload> PAYLOAD_ID = new Type<>(ID);
    public static final StreamCodec<ByteBuf, GlowSearchC2SPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    GlowSearchC2SPayload::query,
                    ByteBufCodecs.BOOL,
                    GlowSearchC2SPayload::matchCase,
                    ByteBufCodecs.BOOL,
                    GlowSearchC2SPayload::clear,
                    ByteBufCodecs.INT,
                    GlowSearchC2SPayload::timer,
                    ByteBufCodecs.INT,
                    GlowSearchC2SPayload::radius,
                    GlowSearchC2SPayload::new
            );

    /**
     * Returns the {@code id} for the Glow Search payload.
     */
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PAYLOAD_ID;
    }
}