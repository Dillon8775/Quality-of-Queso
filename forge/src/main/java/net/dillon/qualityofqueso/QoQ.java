package net.dillon.qualityofqueso;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.EmptyLoadContext;
import net.dillon.qualityofqueso.main.ClientMain;
import net.dillon.qualityofqueso.main.Main;
import net.dillon.qualityofqueso.option.screen.ModOptionsScreen;
import net.dillon.qualityofqueso.registry.ForgeSoundEvents;
import net.dillon.qualityofqueso.util.ModUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModUtil.MOD_ID)
public final class QoQ {

    public QoQ(FMLJavaModLoadingContext context) {
        Balm.initializeMod(ModUtil.MOD_ID, EmptyLoadContext.INSTANCE, Main::initialize);

        ForgeSoundEvents.register(context.getModEventBus());
        context.getModEventBus().addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ForgeSoundEvents::bindCommonReferences);
    }

    @Mod.EventBusSubscriber(modid = ModUtil.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ForgeClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            Balm.initializeMod(ModUtil.MOD_ID, EmptyLoadContext.INSTANCE, ClientMain::cInitialize);

            MinecraftForge.registerConfigScreen(ModOptionsScreen::new);
        }
    }
}