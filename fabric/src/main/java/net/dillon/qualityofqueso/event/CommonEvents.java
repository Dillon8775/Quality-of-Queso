package net.dillon.qualityofqueso.event;

import net.dillon.qualityofqueso.command.ItemFrameSearcherCommand;
import net.dillon.qualityofqueso.packet.ClientPreferencesC2SPacket;
import net.dillon.qualityofqueso.packet.GlowSearchC2SPayload;
import net.dillon.qualityofqueso.server.ServerStorage;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import static net.dillon.qualityofqueso.util.ModUtil.*;

public class CommonEvents {

    /**
     * Registers all {@code QoQ} payloads.
     */
    public static void registerPayloads() {
        PayloadTypeRegistry.serverboundPlay().register(
                GlowSearchC2SPayload.PAYLOAD_ID,
                GlowSearchC2SPayload.CODEC
        );
        PayloadTypeRegistry.serverboundPlay().register(
                ClientPreferencesC2SPacket.PAYLOAD_ID,
                ClientPreferencesC2SPacket.CODEC
        );

        ServerPlayNetworking.registerGlobalReceiver(
                GlowSearchC2SPayload.PAYLOAD_ID,
                (payload, context) -> {
                    handleGlowPayload(payload, context.player());
                }
        );
        ServerPlayNetworking.registerGlobalReceiver(
                ClientPreferencesC2SPacket.PAYLOAD_ID,
                (payload, context) -> {
                    handleClientToServerOptions(payload, context.player().getUUID());
                }
        );

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerStorage.clearPrefs(handler.getPlayer().getUUID());
        });
    }

    /**
     * Registers all {@code QoQ} commands.
     */
    public static void registerCommands() {
        if (coptions().itemFrameSearching) {
            CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
                commandDispatcher.register(ItemFrameSearcherCommand.getItemFrameSearcherCommand(commandRegistryAccess));
            });
        }
    }
}