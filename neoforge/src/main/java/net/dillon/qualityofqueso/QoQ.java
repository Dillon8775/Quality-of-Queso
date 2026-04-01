package net.dillon.qualityofqueso;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.neoforge.platform.runtime.NeoForgeLoadContext;
import net.dillon.qualityofqueso.main.Main;
import net.dillon.qualityofqueso.registry.NeoForgeSoundEvents;
import net.dillon.qualityofqueso.util.ModUtil;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(ModUtil.MOD_ID)
public final class QoQ {

    public QoQ(ModContainer container, IEventBus modEventBus) {
        final var context = new NeoForgeLoadContext(container, modEventBus);
        Balm.initializeMod(ModUtil.MOD_ID, context, Main::initialize);

        NeoForgeSoundEvents.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(NeoForgeSoundEvents::bindCommonReferences);
    }
}
