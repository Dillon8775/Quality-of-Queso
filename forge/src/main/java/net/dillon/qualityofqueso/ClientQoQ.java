package net.dillon.qualityofqueso;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.forge.ForgeLoadContext;
import net.dillon.qualityofqueso.main.ClientMain;
import net.dillon.qualityofqueso.registry.ForgeSoundEvents;
import net.dillon.qualityofqueso.screen.MainMenuScreen;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientQoQ {

    protected static void init() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ForgeSoundEvents.register(modEventBus);
        modEventBus.addListener(ClientQoQ::clientSetup);

        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (minecraft, parent) -> new MainMenuScreen(parent)
                )
        );

        final var context = new ForgeLoadContext(modEventBus);
        Balm.initializeMod(ModConstants.MOD_ID, context, ClientMain::cInitialize);
    }

    private static void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(ForgeSoundEvents::bindCommonReferences);
    }
}