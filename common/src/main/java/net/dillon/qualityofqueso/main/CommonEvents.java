package net.dillon.qualityofqueso.main;

import com.mojang.brigadier.CommandDispatcher;
import net.blay09.mods.balm.api.Balm;
import net.dillon.qualityofqueso.command.ItemFrameSearcherCommand;
import net.dillon.qualityofqueso.option.ModCommonOptions;
import net.dillon.qualityofqueso.packet.*;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

/**
 * Common events for Quality of Queso.
 */
public final class CommonEvents {

    public static void registerCommonCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandRegistryAccess) {
        if (ModCommonOptions.INSTANCE.getInstance().itemFrameSearching) {
            dispatcher.register(ItemFrameSearcherCommand.itemFrameSearcherCommand(commandRegistryAccess));
        }
    }

    public static void registerCommonPackets() {
        Balm.getNetworking().allowClientAndServerOnly(ModConstants.MOD_ID);

        Balm.getNetworking().registerServerboundPacket(
                GlowSearchC2SPacket.PACKET_TYPE,
                GlowSearchC2SPacket.class,
                GlowSearchC2SPacket.CODEC,
                ServerPacketHandlers::sendGlowPacket
        );

        Balm.getNetworking().registerServerboundPacket(
                ClientPreferencesC2SPacket.PACKET_TYPE,
                ClientPreferencesC2SPacket.class,
                ClientPreferencesC2SPacket.CODEC,
                ServerPacketHandlers::handleClientToServerOptions
        );

        Balm.getNetworking().registerServerboundPacket(
                ManualItemPickupC2SPacket.PACKET_TYPE,
                ManualItemPickupC2SPacket.class,
                ManualItemPickupC2SPacket.CODEC,
                ServerPacketHandlers::handleManualItemPickupIntent
        );

        Balm.getNetworking().registerServerboundPacket(
                RequestShulkerStateC2SPacket.PACKET_TYPE,
                RequestShulkerStateC2SPacket.class,
                RequestShulkerStateC2SPacket.CODEC,
                ServerPacketHandlers::handleRequestShulkerState
        );

        Balm.getNetworking().registerServerboundPacket(
                UpdateShulkerStateC2SPacket.PACKET_TYPE,
                UpdateShulkerStateC2SPacket.class,
                UpdateShulkerStateC2SPacket.CODEC,
                ServerPacketHandlers::handleUpdateShulkerState
        );

        Balm.getNetworking().registerClientboundPacket(
                SyncShulkerStateS2CPacket.PACKET_TYPE,
                SyncShulkerStateS2CPacket.class,
                SyncShulkerStateS2CPacket.CODEC,
                ServerPacketHandlers::handleSyncShulkerState
        );
    }
}