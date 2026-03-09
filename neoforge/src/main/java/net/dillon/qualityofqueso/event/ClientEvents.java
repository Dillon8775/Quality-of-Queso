package net.dillon.qualityofqueso.event;

import net.dillon.qualityofqueso.QoQ;
import net.dillon.qualityofqueso.command.ItemFrameSearcherCommand;
import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.util.ModUtil;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

import static net.dillon.qualityofqueso.util.ModUtil.*;

@EventBusSubscriber(modid = QoQ.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void registerKeybindings(RegisterKeyMappingsEvent event) {
        event.register(ModKeybinds.QUICK_EQUIP);
        event.register(ModKeybinds.MOVE_CONTAINER);
        event.register(ModKeybinds.MOVE_INVENTORY);
        event.register(ModKeybinds.SORT_CONTAINER);
        event.register(ModKeybinds.SWAP_ITEMS);
        event.register(ModKeybinds.OPEN_SEARCH_ITEM_FRAMES_GUI);
        event.register(ModKeybinds.HIDE_RECIPE_BOOK);
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent dispatcher) {
        dispatcher.getDispatcher().register(
                ItemFrameSearcherCommand.getItemFrameSearcherCommand(dispatcher.getBuildContext())
        );
    }

    // Load multi-server config
    @SubscribeEvent
    public static void onClientJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        if (uoptions().multiServerConfigs) {
            ModUtil.LOADED = true;
            loadServerConfig();
        }
        if (isOnServer(Minecraft.getInstance()) && options().alwaysPreventRageQuitting) {
            options().preventRageQuitting = true;
            saveAll(Minecraft.getInstance());
        }
    }

    @SubscribeEvent
    public static void onLevelChange(LevelEvent.Load event) {
        resetArmorHudState();
    }

    @SubscribeEvent
    public static void onClientRespawn(ClientPlayerNetworkEvent.Clone event) {
        resetArmorHudState();
    }

    // Unload multi-server config
    @SubscribeEvent
    public static void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        resetArmorHudState();

        if (isOnServer(Minecraft.getInstance())) {
            unloadServerConfig();
        }
    }
}