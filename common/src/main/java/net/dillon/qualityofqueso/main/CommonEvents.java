package net.dillon.qualityofqueso.main;

import com.mojang.brigadier.CommandDispatcher;
import net.blay09.mods.balm.api.Balm;
import net.dillon.qualityofqueso.command.ItemFrameSearcherCommand;
import net.dillon.qualityofqueso.option.ModCommonOptions;
import net.dillon.qualityofqueso.packet.*;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;

/**
 * Common events for Quality of Queso.
 */
public class CommonEvents {

    public static void registerCommonCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandRegistryAccess) {
        if (ModCommonOptions.INSTANCE.getInstance().itemFrameSearching) {
            dispatcher.register(ItemFrameSearcherCommand.itemFrameSearcherCommand(commandRegistryAccess));
        }
    }

    public static void registerCommonPackets() {
        Balm.getNetworking().allowClientAndServerOnly(ModConstants.MOD_ID);

        Balm.getNetworking().registerServerboundPacket(
                new ResourceLocation("qualityofqueso", "glow_search"),
                GlowSearchC2SPacket.class,
                GlowSearchC2SPacket::encode,
                GlowSearchC2SPacket::new,
                ServerPacketHandlers::sendGlowPacket
        );

        Balm.getNetworking().registerServerboundPacket(
                new ResourceLocation("qualityofqueso", "client_preferences"),
                ClientPreferencesC2SPacket.class,
                ClientPreferencesC2SPacket::encode,
                ClientPreferencesC2SPacket::new,
                ServerPacketHandlers::handleClientToServerOptions
        );

        Balm.getNetworking().registerServerboundPacket(
                new ResourceLocation("qualityofqueso", "manual_item_pickup"),
                ManualItemPickupC2SPacket.class,
                ManualItemPickupC2SPacket::encode,
                ManualItemPickupC2SPacket::new,
                ServerPacketHandlers::handleManualItemPickupIntent
        );

        Balm.getNetworking().registerServerboundPacket(
                new ResourceLocation("qualityofqueso", "request_shulker_state"),
                RequestShulkerStateC2SPacket.class,
                RequestShulkerStateC2SPacket::encode,
                RequestShulkerStateC2SPacket::new,
                ServerPacketHandlers::handleRequestShulkerState
        );

        Balm.getNetworking().registerServerboundPacket(
                new ResourceLocation("qualityofqueso", "update_shulker_state"),
                UpdateShulkerStateC2SPacket.class,
                UpdateShulkerStateC2SPacket::encode,
                UpdateShulkerStateC2SPacket::new,
                ServerPacketHandlers::handleUpdateShulkerState
        );

        Balm.getNetworking().registerClientboundPacket(
                new ResourceLocation("qualityofqueso", "sync_shulker_state"),
                SyncShulkerStateS2CPacket.class,
                SyncShulkerStateS2CPacket::encode,
                SyncShulkerStateS2CPacket::new,
                ServerPacketHandlers::handleSyncShulkerState
        );
    }
}