package net.dillon.qualityofqueso.event;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.util.ModUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screens.DeathScreen;

import static net.dillon.qualityofqueso.util.ModUtil.*;

@Environment(EnvType.CLIENT)
public class ClientEvents {

    /**
     * Registers all {@code keybindings.}
     */
    public static void registerKeybindings() {
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
    public static void registerClientEvents() {
        ClientPlayConnectionEvents.JOIN.register((handler, packet, client) -> {
            if (uoptions().main.multiServerConfigs) {
                ModUtil.LOADED = true;
                loadServerConfig();
            }
            if (isOnServer(client) && options().misc.alwaysPreventRageQuitting) {
                options().misc.preventRageQuitting = true;
                saveAll(client);
            }
            sendClientOptionsToServer();
        });

        ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((level, client) -> {
            resetArmorHudState();
        });

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof DeathScreen) {
                resetArmorHudState();
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            resetArmorHudState();

            if (isOnServer(client)) {
                unloadServerConfig();
            }
        });
    }
}