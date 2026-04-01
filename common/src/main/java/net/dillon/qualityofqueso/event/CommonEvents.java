package net.dillon.qualityofqueso.event;

import com.mojang.brigadier.CommandDispatcher;
import net.blay09.mods.balm.Balm;
import net.dillon.qualityofqueso.command.ItemFrameSearcherCommand;
import net.dillon.qualityofqueso.packet.ClientPreferencesC2SPacket;
import net.dillon.qualityofqueso.packet.GlowSearchC2SPacket;
import net.dillon.qualityofqueso.util.ModUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

/**
 * Common events for Quality of Queso.
 */
public class CommonEvents {

    public static void registerPackets() {
        Balm.networking().registerServerboundPacket(
                GlowSearchC2SPacket.PACKET_TYPE,
                GlowSearchC2SPacket.class,
                GlowSearchC2SPacket.CODEC,
                ModUtil::handleGlowPayload
        );
        Balm.networking().registerServerboundPacket(
                ClientPreferencesC2SPacket.PACKET_TYPE,
                ClientPreferencesC2SPacket.class,
                ClientPreferencesC2SPacket.CODEC,
                ModUtil::handleClientToServerOptions
        );
    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandRegistryAccess) {
        dispatcher.register(ItemFrameSearcherCommand.getItemFrameSearcherCommand(commandRegistryAccess));
    }
}