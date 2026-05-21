package net.dillon.qualityofqueso.packet;

import net.minecraft.network.FriendlyByteBuf;

/**
 * Sends the player's client-side options to the server.
 */
public record ClientPreferencesC2SPacket(boolean includeHotbar, boolean perpendicularQuickMoving, String lockInventory) {

    /**
     * Decoding for client preferences packet.
     */
    public ClientPreferencesC2SPacket(FriendlyByteBuf buf) {
        this(buf.readBoolean(), buf.readBoolean(), buf.readUtf());
    }

    /**
     * Encoding for client preferences packet.
     */
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(this.includeHotbar);
        buf.writeBoolean(this.perpendicularQuickMoving);
        buf.writeUtf(this.lockInventory);
    }
}