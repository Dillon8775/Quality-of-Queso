package net.dillon.qualityofqueso.event;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.main.ClientEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class FabricClientEvents {

    /**
     * Registers all {@code keybindings.}
     */
    public static void registerFabricKeyBindings() {
        KeyBindingHelper.registerKeyBinding(ModKeybinds.LOCK_SLOT);
        KeyBindingHelper.registerKeyBinding(ModKeybinds.MOVE_TO_CONTAINER);
        KeyBindingHelper.registerKeyBinding(ModKeybinds.MOVE_TO_INVENTORY);
        KeyBindingHelper.registerKeyBinding(ModKeybinds.OPEN_SEARCH_ITEM_FRAMES_GUI);
        KeyBindingHelper.registerKeyBinding(ModKeybinds.QUICK_EQUIP);
        KeyBindingHelper.registerKeyBinding(ModKeybinds.SORT);
        KeyBindingHelper.registerKeyBinding(ModKeybinds.SWAP_ITEMS);
        KeyBindingHelper.registerKeyBinding(ModKeybinds.VIEW_LAST_KNOWN_ENDER_CHEST);
    }

    /**
     * Registers all {@code client-side events.}
     */
    public static void registerFabricClientEvents() {
        ClientPlayConnectionEvents.JOIN.register((handler, packet, minecraft) -> {
            ClientEvents.onPlayerJoin(minecraft);

            if (!ClientPlayNetworking.canSend(new ResourceLocation("qualityofqueso", "glow_search"))) {
                ClientEvents.warnModNotPresent(minecraft);
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, minecraft) -> {
            ClientEvents.onPlayerDisconnect(minecraft);
        });
    }
}