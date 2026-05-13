package net.dillon.qualityofqueso.event;

import net.dillon.qualityofqueso.main.CommonEvents;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID)
public class ForgeCommonEvents {

    @SubscribeEvent
    public static void registerCommandsForge(RegisterCommandsEvent dispatcher) {
        CommonEvents.registerCommonCommands(dispatcher.getDispatcher(), dispatcher.getBuildContext());
    }
}