package net.dillon.qualityofqueso.packet;

import net.minecraft.network.FriendlyByteBuf;

/**
 * The payload (or packet) for taking in the data required to make item frames glow.
 */
public record GlowSearchC2SPacket(String query, boolean matchCase, boolean clear, int timer, int radius) {

    /**
     * Decoding for glow search packet.
     */
    public GlowSearchC2SPacket(FriendlyByteBuf buf) {
        this(buf.readUtf(), buf.readBoolean(), buf.readBoolean(), buf.readInt(), buf.readInt());
    }

    /**
     * Encoding for glow search packet.
     */
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.query);
        buf.writeBoolean(this.matchCase);
        buf.writeBoolean(this.clear);
        buf.writeInt(this.timer);
        buf.writeInt(this.radius);
    }
}