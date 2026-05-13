package net.dillon.qualityofqueso.event;

import net.dillon.qualityofqueso.main.CommonEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class FabricCommonEvents {

    public static void registerFabricCommands() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            CommonEvents.registerCommonCommands(commandDispatcher, commandRegistryAccess);
        });
    }
}