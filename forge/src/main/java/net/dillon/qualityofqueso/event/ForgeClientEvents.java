package net.dillon.qualityofqueso.event;

import net.dillon.qualityofqueso.helper.ModHelper;
import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.main.ClientEvents;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ForgeClientEvents {

    @Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ForgeClientModEvents {

        @SubscribeEvent
        public static void registerForgeKeybindings(RegisterKeyMappingsEvent event) {
            event.register(ModKeybinds.LOCK_SLOT);
            event.register(ModKeybinds.MOVE_TO_CONTAINER);
            event.register(ModKeybinds.MOVE_TO_INVENTORY);
            event.register(ModKeybinds.OPEN_SEARCH_ITEM_FRAMES_GUI);
            event.register(ModKeybinds.OPEN_QUALITY_OF_QUESO_MAIN_MENU);
            event.register(ModKeybinds.OPEN_VISUAL_TIME_GUI);
            event.register(ModKeybinds.QUICK_EQUIP);
            event.register(ModKeybinds.SORT);
            event.register(ModKeybinds.SWAP_ITEMS);
            event.register(ModKeybinds.VIEW_LAST_KNOWN_ENDER_CHEST);
        }
    }

    @Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, value = Dist.CLIENT)
    public static class ForgeClientBusEvents {

        @SubscribeEvent
        public static void onClientJoin(ClientPlayerNetworkEvent.LoggingIn event) {
            Minecraft minecraft = Minecraft.getInstance();
            ClientEvents.onPlayerJoin(minecraft);
        }

        @SubscribeEvent
        public static void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
            ClientEvents.onPlayerDisconnect(Minecraft.getInstance());
        }

        @SubscribeEvent
        public static void onLevelChange(LevelEvent.Load event) {
            ModHelper.resetArmorHudState();
        }
    }
}