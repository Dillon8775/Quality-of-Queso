package net.dillon.qualityofqueso.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

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
public record UpdateShulkerStateC2SPacket(BlockPos pos, boolean filtered, boolean tagMode, String filterItemsCsv, String lockedSlotsCsv, String sortingMode) implements CustomPacketPayload {
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("qualityofqueso", "update_shulker_state");
    public static final Type<UpdateShulkerStateC2SPacket> PACKET_TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateShulkerStateC2SPacket> CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC,
                    UpdateShulkerStateC2SPacket::pos,
                    ByteBufCodecs.BOOL,
                    UpdateShulkerStateC2SPacket::filtered,
                    ByteBufCodecs.BOOL,
                    UpdateShulkerStateC2SPacket::tagMode,
                    ByteBufCodecs.STRING_UTF8,
                    UpdateShulkerStateC2SPacket::filterItemsCsv,
                    ByteBufCodecs.STRING_UTF8,
                    UpdateShulkerStateC2SPacket::lockedSlotsCsv,
                    ByteBufCodecs.STRING_UTF8,
                    UpdateShulkerStateC2SPacket::sortingMode,
                    UpdateShulkerStateC2SPacket::new
            );

    /**
     * @return the registered payload type for this packet.
     */
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}