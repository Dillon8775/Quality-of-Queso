package net.dillon.qualityofqueso.event;

import net.dillon.qualityofqueso.util.ModUtil;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModUtil.MOD_ID)
public class ForgeCommonEvents {

    @SubscribeEvent
    public static void registerCommandsForge(RegisterCommandsEvent dispatcher) {
        CommonEvents.registerCommands(dispatcher.getDispatcher(), dispatcher.getBuildContext());
    }
}