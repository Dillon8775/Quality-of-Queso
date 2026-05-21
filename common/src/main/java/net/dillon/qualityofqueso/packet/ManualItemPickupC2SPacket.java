package net.dillon.qualityofqueso.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Requests manual pickup for a specific item entity id.
 */
public record ManualItemPickupC2SPacket(int entityId) implements CustomPacketPayload {
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("qualityofqueso", "manual_item_pickup");
    public static final Type<ManualItemPickupC2SPacket> PACKET_TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, ManualItemPickupC2SPacket> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT,
                    ManualItemPickupC2SPacket::entityId,
                    ManualItemPickupC2SPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}