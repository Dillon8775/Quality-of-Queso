package net.dillon.qualityofqueso.packet;

import net.dillon.dillonlib.annotation.Dill;
import net.dillon.dillonlib.annotation.DillType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Server-to-client packet with authoritative QoQ state for a specific shulker.
 *
 * @param pos            the shulker block position.
 * @param filtered       whether filtering is enabled.
 * @param tagMode        whether the filter mode is tag-based.
 * @param filterItemsCsv comma-separated item identifiers for placeholders.
 * @param lockedSlotsCsv comma-separated locked slot indices.
 * @param sortingMode    current sorting mode enum name.
 */
@Dill(DillType.COMMON)
public record SyncShulkerStateS2CPacket(BlockPos pos, boolean filtered, boolean tagMode, String filterItemsCsv, String lockedSlotsCsv, String sortingMode) implements CustomPacketPayload {
    private static final Identifier ID = Identifier.fromNamespaceAndPath("qualityofqueso", "sync_shulker_state");
    public static final Type<SyncShulkerStateS2CPacket> PACKET_TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncShulkerStateS2CPacket> CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC,
                    SyncShulkerStateS2CPacket::pos,
                    ByteBufCodecs.BOOL,
                    SyncShulkerStateS2CPacket::filtered,
                    ByteBufCodecs.BOOL,
                    SyncShulkerStateS2CPacket::tagMode,
                    ByteBufCodecs.STRING_UTF8,
                    SyncShulkerStateS2CPacket::filterItemsCsv,
                    ByteBufCodecs.STRING_UTF8,
                    SyncShulkerStateS2CPacket::lockedSlotsCsv,
                    ByteBufCodecs.STRING_UTF8,
                    SyncShulkerStateS2CPacket::sortingMode,
                    SyncShulkerStateS2CPacket::new
            );

    /**
     * @return the registered payload type for this packet.
     */
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}