package net.dillon.qualityofqueso.event;

import net.dillon.qualityofqueso.util.ModUtil;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = ModUtil.MOD_ID)
public class NeoForgeCommonEvents {

    @SubscribeEvent
    public static void registerCommandsNeoForged(RegisterCommandsEvent dispatcher) {
        CommonEvents.registerCommands(dispatcher.getDispatcher(), dispatcher.getBuildContext());
    }
}