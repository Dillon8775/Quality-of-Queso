package net.dillon.qualityofqueso.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import static net.dillon.qualityofqueso.util.ModUtil.ofQoQ;

/**
 * Sends the player's client-side options to the server.
 */
public record ClientPreferencesC2SPacket(boolean includeHotbar, boolean perpendicularQuickMoving) implements CustomPacketPayload {
    public static final Identifier ID = ofQoQ("client_preferences_c2s");
    public static final Type<ClientPreferencesC2SPacket> PAYLOAD_ID = new Type<>(ID);
    public static final StreamCodec<ByteBuf, ClientPreferencesC2SPacket> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,
                    ClientPreferencesC2SPacket::includeHotbar,
                    ByteBufCodecs.BOOL,
                    ClientPreferencesC2SPacket::perpendicularQuickMoving,
                    ClientPreferencesC2SPacket::new
            );

    /**
     * Returns the {@code id} for the Client Preferences payload.
     */
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PAYLOAD_ID;
    }
}