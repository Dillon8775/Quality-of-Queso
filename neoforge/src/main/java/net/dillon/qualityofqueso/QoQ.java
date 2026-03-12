package net.dillon.qualityofqueso;

import net.dillon.qualityofqueso.debug.ModHudEntries;
import net.dillon.qualityofqueso.option.instance.ModClientOptions;
import net.dillon.qualityofqueso.option.instance.ModCommonOptions;
import net.dillon.qualityofqueso.option.instance.TrackedContainers;
import net.dillon.qualityofqueso.option.instance.UniversalOptions;
import net.dillon.qualityofqueso.registry.NeoForgeSoundEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(QoQ.MOD_ID)
public final class QoQ {
    public static final String MOD_ID = "qualityofqueso";

    /**
     * Main entrypoint for QoQ (NeoForge).
     */
    public QoQ(IEventBus modEventBus, ModContainer container) {
        NeoForgeSoundEvents.register(modEventBus);
        modEventBus.addListener(this::commonSetup);

        if (FMLEnvironment.getDist().isClient()) {
            if (ModClientOptions.CLIENT.getInstance() == null) {
                ModClientOptions.CLIENT.setInstance(new ModClientOptions());
            }
            if (TrackedContainers.TRACKED_CONTAINERS.getInstance() == null) {
                TrackedContainers.TRACKED_CONTAINERS.setInstance(new TrackedContainers());
            }
            if (UniversalOptions.UNIVERSAL.getInstance() == null) {
                UniversalOptions.UNIVERSAL.setInstance(new UniversalOptions());
            }
            ModHudEntries.registerDebugEntries();
        }
        if (ModCommonOptions.COMMON.getInstance() == null) {
            ModCommonOptions.COMMON.setInstance(new ModCommonOptions());
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(NeoForgeSoundEvents::bindCommonReferences);
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }

    @EventBusSubscriber(modid = MOD_ID, value = Dist.DEDICATED_SERVER)
    public static class ServerModEvents {

        @SubscribeEvent
        public static void onServerSetup(FMLDedicatedServerSetupEvent event) {
        }
    }
}
