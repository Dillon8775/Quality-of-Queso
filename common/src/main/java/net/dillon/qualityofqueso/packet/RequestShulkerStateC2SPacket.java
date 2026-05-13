package net.dillon.qualityofqueso.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Client-to-server packet requesting the authoritative QoQ state for a shulker at a position.
 *
 * @param pos the shulker block position.
 */
public record RequestShulkerStateC2SPacket(BlockPos pos) {

    /**
     * Decoding for client preferences packet.
     */
    public RequestShulkerStateC2SPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos());
    }

    /**
     * Encoding for client preferences packet.
     */
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }
}