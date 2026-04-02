package net.dillon.qualityofqueso;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.neoforge.platform.runtime.NeoForgeLoadContext;
import net.dillon.qualityofqueso.main.ClientMain;
import net.dillon.qualityofqueso.option.screen.ModOptionsScreen;
import net.dillon.qualityofqueso.registry.NeoForgeSoundEvents;
import net.dillon.qualityofqueso.util.ModUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = ModUtil.MOD_ID, dist = Dist.CLIENT)
public class ClientQoQ {

    public ClientQoQ(ModContainer container, IEventBus modEventBus) {
        final var context = new NeoForgeLoadContext(container, modEventBus);
        Balm.initializeMod(ModUtil.MOD_ID, context, ClientMain::cInitialize);

        NeoForgeSoundEvents.register(modEventBus);
        modEventBus.addListener(this::clientSetup);

        container.registerExtensionPoint(
                IConfigScreenFactory.class,
                (mc, parent) -> new ModOptionsScreen(parent)
        );
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(NeoForgeSoundEvents::bindCommonReferences);
    }
}