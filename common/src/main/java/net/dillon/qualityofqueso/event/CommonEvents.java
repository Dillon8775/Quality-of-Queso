package net.dillon.qualityofqueso.event;

import com.mojang.brigadier.CommandDispatcher;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.PlayerLogoutEvent;
import net.dillon.qualityofqueso.command.ItemFrameSearcherCommand;
import net.dillon.qualityofqueso.packet.ClientPreferencesC2SPacket;
import net.dillon.qualityofqueso.packet.GlowSearchC2SPacket;
import net.dillon.qualityofqueso.server.ServerStorage;
import net.dillon.qualityofqueso.util.ModUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

import static net.dillon.qualityofqueso.util.ModUtil.coptions;

/**
 * Common events for Quality of Queso.
 */
public class CommonEvents {

    public static void registerCommonEvents() {
        Balm.getEvents().onEvent(PlayerLogoutEvent.class, (event) -> {
            ServerStorage.clearPrefs(event.getPlayer().getUUID());
        });
    }

    public static void registerPackets() {
        Balm.getNetworking().allowClientAndServerOnly(ModUtil.MOD_ID);

        Balm.getNetworking().registerServerboundPacket(
                GlowSearchC2SPacket.PACKET_TYPE,
                GlowSearchC2SPacket.class,
                GlowSearchC2SPacket.CODEC,
                ModUtil::sendGlowPacket
        );

        Balm.getNetworking().registerServerboundPacket(
                ClientPreferencesC2SPacket.PACKET_TYPE,
                ClientPreferencesC2SPacket.class,
                ClientPreferencesC2SPacket.CODEC,
                ModUtil::handleClientToServerOptions
        );
    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandRegistryAccess) {
        if (coptions().itemFrameSearching) {
            dispatcher.register(ItemFrameSearcherCommand.getItemFrameSearcherCommand(commandRegistryAccess));
        }
    }
}