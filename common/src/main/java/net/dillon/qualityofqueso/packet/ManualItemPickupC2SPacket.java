package net.dillon.qualityofqueso.packet;

import net.minecraft.network.FriendlyByteBuf;

/**
 * Requests manual pickup for a specific item entity id.
 */
public record ManualItemPickupC2SPacket(int entityId) {

    /**
     * Decoding for manual item pickup packet.
     */
    public ManualItemPickupC2SPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    /**
     * Encoding for manual item pickup packet.
     */
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
    }
}