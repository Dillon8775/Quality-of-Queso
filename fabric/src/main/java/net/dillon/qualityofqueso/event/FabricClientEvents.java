package net.dillon.qualityofqueso.event;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

@Environment(EnvType.CLIENT)
public class FabricClientEvents {

    public static void registerFabricKeyMappings() {
        KeyBindingHelper.registerKeyBinding(ModKeybinds.QUICK_EQUIP);
        KeyBindingHelper.registerKeyBinding(ModKeybinds.MOVE_CONTAINER);
        KeyBindingHelper.registerKeyBinding(ModKeybinds.MOVE_INVENTORY);
        KeyBindingHelper.registerKeyBinding(ModKeybinds.SORT_CONTAINER);
        KeyBindingHelper.registerKeyBinding(ModKeybinds.SWAP_ITEMS);
        KeyBindingHelper.registerKeyBinding(ModKeybinds.OPEN_SEARCH_ITEM_FRAMES_GUI);
        KeyBindingHelper.registerKeyBinding(ModKeybinds.HIDE_RECIPE_BOOK);
    }
}