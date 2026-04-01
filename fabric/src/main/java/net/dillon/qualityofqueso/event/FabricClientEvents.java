package net.dillon.qualityofqueso.event;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

@Environment(EnvType.CLIENT)
public class FabricClientEvents {

    /**
     * Registers all {@code keybindings.}
     */
    public static void registerFabricKeyBindings() {
        KeyMappingHelper.registerKeyMapping(ModKeybinds.QUICK_EQUIP);
        KeyMappingHelper.registerKeyMapping(ModKeybinds.MOVE_CONTAINER);
        KeyMappingHelper.registerKeyMapping(ModKeybinds.MOVE_INVENTORY);
        KeyMappingHelper.registerKeyMapping(ModKeybinds.SORT_CONTAINER);
        KeyMappingHelper.registerKeyMapping(ModKeybinds.SWAP_ITEMS);
        KeyMappingHelper.registerKeyMapping(ModKeybinds.OPEN_SEARCH_ITEM_FRAMES_GUI);
        KeyMappingHelper.registerKeyMapping(ModKeybinds.HIDE_RECIPE_BOOK);
    }

    /**
     * Registers all {@code client-side events.}
     */
    public static void registerFabricClientEvents() {
        ClientPlayConnectionEvents.JOIN.register((handler, packet, minecraft) -> {
            ClientEvents.onPlayerJoin(minecraft);
        });

        ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((level, minecraft) -> {
            ClientEvents.afterLevelChangeOrRespawn();
        });

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ClientEvents.afterScreenInit(screen);
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, minecraft) -> {
            ClientEvents.onPlayerDisconnect(minecraft);
        });
    }
}