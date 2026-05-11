package net.dillon.qualityofqueso;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.neoforge.platform.runtime.NeoForgeLoadContext;
import net.dillon.qualityofqueso.keybind.ModKeyMappings;
import net.dillon.qualityofqueso.main.ClientMain;
import net.dillon.qualityofqueso.registry.NeoForgeSoundEvents;
import net.dillon.qualityofqueso.screen.MainMenuScreen;
import net.dillon.qualityofqueso.util.ModConstants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = ModConstants.MOD_ID, dist = Dist.CLIENT)
public class ClientQoQ {

    public ClientQoQ(ModContainer container, IEventBus modEventBus) {
        ModKeyMappings.initKeybinds();
        NeoForgeSoundEvents.register(modEventBus);
        modEventBus.addListener(this::clientSetup);

        container.registerExtensionPoint(
                IConfigScreenFactory.class,
                (mc, parent) -> new MainMenuScreen(parent)
        );

        final var context = new NeoForgeLoadContext(container, modEventBus);
        Balm.initializeMod(ModConstants.MOD_ID, context, ClientMain::cInitialize);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(NeoForgeSoundEvents::bindCommonReferences);
    }
}