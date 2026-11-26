package net.dillon.qualityofqueso.main;

import net.blay09.mods.balm.core.BalmRegistrars;
import net.dillon.dillonlib.client.command.ReloadConfigCommand;
import net.dillon.dillonlib.util.BaseOptions;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.option.*;
import net.dillon.qualityofqueso.sound.ModSoundEvents;

import java.util.ArrayList;
import java.util.List;

/**
 * The client-side entrypoint for Quality of Queso.
 */
public class ClientMain {

    /**
     * Initializes client-side features.
     */
    public static void cInitialize(BalmRegistrars registrars) {
        ModSoundEvents.registerClientSounds(registrars);

        loadClientConfigs();
    }

    /**
     * Loads all client configs.
     */
    private static void loadClientConfigs() {
        List<ModBaseOptionsHandler<?>> handlers = List.of(
                ModClientOptions.INSTANCE,
                UniversalOptions.INSTANCE,
                ContainerData.INSTANCE,
                LockedContainerSlots.INSTANCE,
                LockedPlayerSlots.INSTANCE,
                MixinOptions.INSTANCE
        );

        List<BaseOptions<?>> instances = new ArrayList<>();
        for (ModBaseOptionsHandler<?> handler : handlers) {
            handler.load();
            instances.add(handler);
        }

        ReloadConfigCommand.createReloadable(
                ModConstants.MOD_ID,
                instances
        );
    }
}