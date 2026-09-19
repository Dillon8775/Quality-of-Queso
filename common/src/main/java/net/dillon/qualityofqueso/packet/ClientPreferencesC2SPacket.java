package net.dillon.qualityofqueso.packet;

import net.dillon.dillonlib.annotation.Dill;
import net.dillon.dillonlib.annotation.DillType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import static net.dillon.qualityofqueso.helper.ModHelper.qoqIdentifier;

/**
 * Sends the player's client-side options to the server.
 */
@Dill(DillType.COMMON)
public record ClientPreferencesC2SPacket(
        boolean includeHotbar,
        boolean perpendicularQuickMoving,
        String lockInventory
) implements CustomPacketPayload {
    private static final Identifier ID = qoqIdentifier("client_preferences");
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

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}