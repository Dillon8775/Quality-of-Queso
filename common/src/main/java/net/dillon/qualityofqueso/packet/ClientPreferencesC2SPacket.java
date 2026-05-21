package net.dillon.qualityofqueso.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Sends the player's client-side options to the server.
 */
public record ClientPreferencesC2SPacket(boolean includeHotbar,
                                         boolean perpendicularQuickMoving,
                                         String lockInventory) implements CustomPacketPayload {
    private static final Identifier ID = Identifier.fromNamespaceAndPath("qualityofqueso", "client_preferences");
    public static final Type<ClientPreferencesC2SPacket> PACKET_TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientPreferencesC2SPacket> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,
                    ClientPreferencesC2SPacket::includeHotbar,
                    ByteBufCodecs.BOOL,
                    ClientPreferencesC2SPacket::perpendicularQuickMoving,
                    ByteBufCodecs.STRING_UTF8,
                    ClientPreferencesC2SPacket::lockInventory,
                    ClientPreferencesC2SPacket::new
            );

    /**
     * Returns the {@code id} for the Client Preferences payload.
     */
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}