package net.dillon.qualityofqueso.event;

import net.dillon.qualityofqueso.main.CommonEvents;
import net.dillon.qualityofqueso.util.ModConstants;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = ModConstants.MOD_ID)
public class NeoForgeCommonEvents {

    @SubscribeEvent
    public static void registerCommandsNeoForged(RegisterCommandsEvent dispatcher) {
        CommonEvents.registerCommonCommands(dispatcher.getDispatcher(), dispatcher.getBuildContext());
    }
}