package net.dillon.qualityofqueso.event;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import static net.dillon.qualityofqueso.util.ModUtil.coptions;

public class FabricCommonEvents {

    public static void registerFabricCommands() {
        if (coptions().itemFrameSearching) {
            CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
                CommonEvents.registerCommands(commandDispatcher, commandRegistryAccess);
            });
        }
    }
}