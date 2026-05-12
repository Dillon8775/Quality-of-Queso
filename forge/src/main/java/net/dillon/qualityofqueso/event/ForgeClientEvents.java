package net.dillon.qualityofqueso.event;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.util.ModUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.dillon.qualityofqueso.util.ModUtil.resetArmorHudState;

public class ForgeClientEvents {

    @Mod.EventBusSubscriber(modid = ModUtil.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ForgeClientModEvents {

        @SubscribeEvent
        public static void registerForgeKeybindings(RegisterKeyMappingsEvent event) {
            event.register(ModKeybinds.QUICK_EQUIP);
            event.register(ModKeybinds.MOVE_CONTAINER);
            event.register(ModKeybinds.MOVE_INVENTORY);
            event.register(ModKeybinds.SORT_CONTAINER);
            event.register(ModKeybinds.SWAP_ITEMS);
            event.register(ModKeybinds.OPEN_SEARCH_ITEM_FRAMES_GUI);
            event.register(ModKeybinds.HIDE_RECIPE_BOOK);
        }
    }

    @Mod.EventBusSubscriber(modid = ModUtil.MOD_ID, value = Dist.CLIENT)
    public static class ForgeClientBusEvents {

        @SubscribeEvent
        public static void onLevelChange(LevelEvent.Load event) {
            resetArmorHudState();
        }
    }
}