package net.dillon.qualityofqueso.packet;

import net.minecraft.network.FriendlyByteBuf;

/**
 * Sends the player's client-side options to the server.
 */
public record ClientPreferencesC2SPacket(boolean includeHotbar, boolean perpendicularQuickMoving) {

    /**
     * Decoding for client preferences packet.
     */
    public ClientPreferencesC2SPacket(FriendlyByteBuf buf) {
        this(buf.readBoolean(), buf.readBoolean());
    }

    /**
     * Encoding for client preferences packet.
     */
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(this.includeHotbar);
        buf.writeBoolean(this.perpendicularQuickMoving);
    }
}