package net.dillon.qualityofqueso.event;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class FabricCommonEvents {

    public static void registerFabricCommands() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            CommonEvents.registerCommands(commandDispatcher, commandRegistryAccess);
        });
    }
}