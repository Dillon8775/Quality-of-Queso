package net.dillon.qualityofqueso.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Client-to-server packet updating QoQ state for a specific shulker.
 *
 * @param pos            the shulker block position.
 * @param filtered       whether filtering is enabled for this shulker.
 * @param tagMode        whether the filter mode is tag-based.
 * @param filterItemsCsv comma-separated item identifiers for filter placeholders.
 * @param lockedSlotsCsv comma-separated locked slot indices.
 * @param sortingMode    current sorting mode enum name.
 */
public record UpdateShulkerStateC2SPacket(BlockPos pos, boolean filtered, boolean tagMode, String filterItemsCsv, String lockedSlotsCsv, String sortingMode) {

    /**
     * Decoding for client preferences packet.
     */
    public UpdateShulkerStateC2SPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readBoolean(), buf.readBoolean(), buf.readUtf(), buf.readUtf(), buf.readUtf());
    }

    /**
     * Encoding for client preferences packet.
     */
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeBoolean(this.filtered);
        buf.writeBoolean(this.tagMode);
        buf.writeUtf(this.filterItemsCsv);
        buf.writeUtf(this.lockedSlotsCsv);
        buf.writeUtf(this.sortingMode);
    }
}