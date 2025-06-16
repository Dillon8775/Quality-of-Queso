package net.dillon.qualityofqueso.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * The payload (or packet) for taking in the data required to make item frames glow.
 */
public record GlowSearchC2SPayload(String query, boolean clear, int timer, int radius) implements CustomPayload {
    public static final CustomPayload.Id<GlowSearchC2SPayload> ID =
        new CustomPayload.Id<>(Identifier.of("qualityofqueso:glow_search"));

    static final PacketCodec<RegistryByteBuf, GlowSearchC2SPayload> CODEC =
        PacketCodec.of(
            (buf, payload) -> {
                payload.writeString(buf.query);
                payload.writeBoolean(buf.clear);
                payload.writeInt(buf.timer);
                payload.writeInt(buf.radius);
            },
            buf -> new GlowSearchC2SPayload(buf.readString(), buf.readBoolean(), buf.readInt(), buf.readInt())
        );

    /**
     * Returns the {@code id} for the Glow Search payload.
     */
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}