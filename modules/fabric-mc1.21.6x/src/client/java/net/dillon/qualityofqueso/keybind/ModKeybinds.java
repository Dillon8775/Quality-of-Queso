package net.dillon.qualityofqueso.keybind;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

/**
 * Keybindings for the {@code Quality of Queso} mod.
 */
@Environment(EnvType.CLIENT)
public class ModKeybinds {
    public static final String QOQ = "qualityofqueso.gui.options.title";

    public static final KeyBinding QUICK_EQUIP = KeyBindingHelper.registerKeyBinding(new KeyBinding("qualityofqueso.quick_equip", InputUtil.GLFW_KEY_B, QOQ));
    public static final KeyBinding MOVE_CONTAINER = KeyBindingHelper.registerKeyBinding(new KeyBinding("qualityofqueso.move_container", InputUtil.GLFW_KEY_C, QOQ));
    public static final KeyBinding MOVE_INVENTORY = KeyBindingHelper.registerKeyBinding(new KeyBinding("qualityofqueso.move_inventory", InputUtil.GLFW_KEY_I, QOQ));
    public static final KeyBinding SWAP_ITEMS = KeyBindingHelper.registerKeyBinding(new KeyBinding("qualityofqueso.swap_items", InputUtil.GLFW_KEY_S, QOQ));
    public static final KeyBinding OPEN_SEARCH_ITEM_FRAMES_GUI = KeyBindingHelper.registerKeyBinding(new KeyBinding("qualityofqueso.open_item_frame_search_gui", InputUtil.GLFW_KEY_I, QOQ));

    /**
     * Initialize mod keybinds.
     */
    public static void init() {
    }
}