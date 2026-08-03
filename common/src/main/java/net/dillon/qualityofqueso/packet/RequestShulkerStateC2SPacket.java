package net.dillon.qualityofqueso.packet;

import net.dillon.dillonlib.annotation.Dill;
import net.dillon.dillonlib.annotation.DillType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Client-to-server packet requesting the authoritative QoQ state for a shulker at a position.
 *
 * @param pos the shulker block position.
 */
@Dill(DillType.COMMON)
public record RequestShulkerStateC2SPacket(BlockPos pos) implements CustomPacketPayload {
    private static final Identifier ID = Identifier.fromNamespaceAndPath("qualityofqueso", "request_shulker_state");
    public static final Type<RequestShulkerStateC2SPacket> PACKET_TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestShulkerStateC2SPacket> CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC,
                    RequestShulkerStateC2SPacket::pos,
                    RequestShulkerStateC2SPacket::new
            );

    /**
     * @return the registered payload type for this packet.
     */
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}