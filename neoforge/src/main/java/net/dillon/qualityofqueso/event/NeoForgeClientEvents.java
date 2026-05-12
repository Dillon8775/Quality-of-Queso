package net.dillon.qualityofqueso.event;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.util.ModUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

import static net.dillon.qualityofqueso.util.ModUtil.resetArmorHudState;

@EventBusSubscriber(modid = ModUtil.MOD_ID, value = Dist.CLIENT)
public class NeoForgeClientEvents {

    @SubscribeEvent
    public static void registerNeoForgedKeybindings(RegisterKeyMappingsEvent event) {
        event.register(ModKeybinds.QUICK_EQUIP);
        event.register(ModKeybinds.MOVE_CONTAINER);
        event.register(ModKeybinds.MOVE_INVENTORY);
        event.register(ModKeybinds.SORT_CONTAINER);
        event.register(ModKeybinds.SWAP_ITEMS);
        event.register(ModKeybinds.OPEN_SEARCH_ITEM_FRAMES_GUI);
        event.register(ModKeybinds.HIDE_RECIPE_BOOK);
    }

    @SubscribeEvent
    public static void onLevelChange(LevelEvent.Load event) {
        resetArmorHudState();
    }
}