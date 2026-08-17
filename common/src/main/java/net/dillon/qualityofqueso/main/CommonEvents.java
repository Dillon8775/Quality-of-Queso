package net.dillon.qualityofqueso.main;

import net.blay09.mods.balm.Balm;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.packet.*;

/**
 * Common events for Quality of Queso.
 */
public class CommonEvents {

    public static void registerCommonPackets() {
        Balm.networking().allowClientAndServerOnly(ModConstants.MOD_ID);

        Balm.networking().registerServerboundPacket(
                GlowSearchC2SPacket.PACKET_TYPE,
                GlowSearchC2SPacket.class,
                GlowSearchC2SPacket.CODEC,
                ServerPacketHandlers::sendGlowPacket
        );

        Balm.networking().registerServerboundPacket(
                ClientPreferencesC2SPacket.PACKET_TYPE,
                ClientPreferencesC2SPacket.class,
                ClientPreferencesC2SPacket.CODEC,
                ServerPacketHandlers::handleClientToServerOptions
        );

        Balm.networking().registerServerboundPacket(
                ManualItemPickupC2SPacket.PACKET_TYPE,
                ManualItemPickupC2SPacket.class,
                ManualItemPickupC2SPacket.CODEC,
                ServerPacketHandlers::handleManualItemPickupIntent
        );

        Balm.networking().registerServerboundPacket(
                RequestShulkerStateC2SPacket.PACKET_TYPE,
                RequestShulkerStateC2SPacket.class,
                RequestShulkerStateC2SPacket.CODEC,
                ServerPacketHandlers::handleRequestShulkerState
        );

        Balm.networking().registerServerboundPacket(
                UpdateShulkerStateC2SPacket.PACKET_TYPE,
                UpdateShulkerStateC2SPacket.class,
                UpdateShulkerStateC2SPacket.CODEC,
                ServerPacketHandlers::handleUpdateShulkerState
        );

        Balm.networking().registerClientboundPacket(
                SyncShulkerStateS2CPacket.PACKET_TYPE,
                SyncShulkerStateS2CPacket.class,
                SyncShulkerStateS2CPacket.CODEC,
                ServerPacketHandlers::handleSyncShulkerState
        );
    }
}