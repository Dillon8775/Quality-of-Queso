package net.dillon.qualityofqueso.keybind;

import net.dillon.qualityofqueso.main.QualityOfQueso;
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
    public static final KeyBinding QUICK_EQUIP = KeyBindingHelper.registerKeyBinding(new KeyBinding("qualityofqueso.quick_equip", InputUtil.GLFW_KEY_B, QualityOfQueso.TITLE));
    public static final KeyBinding OPEN_SEARCH_ITEM_FRAMES_GUI = KeyBindingHelper.registerKeyBinding(new KeyBinding("qualityofqueso.open_item_frame_search_gui", InputUtil.GLFW_KEY_I, QualityOfQueso.TITLE));

    /**
     * Initialize mod keybinds.
     */
    public static void init() {}
}