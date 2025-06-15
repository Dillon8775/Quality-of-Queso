package net.dillon.qualityofqueso.keybind;

import net.dillon.qualityofqueso.QualityOfQuesoClient;
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
    public static final KeyBinding QUICK_EQUIP = KeyBindingHelper.registerKeyBinding(new KeyBinding("qualityofqueso.quick_equip", InputUtil.GLFW_KEY_B, QualityOfQuesoClient.TITLE));
    public static final KeyBinding OPEN_SEARCH_ITEM_FRAMES_GUI = KeyBindingHelper.registerKeyBinding(new KeyBinding("qualityofqueso.open_search_item_frames_gui", InputUtil.GLFW_KEY_I, QualityOfQuesoClient.TITLE));

    /**
     * Initialize mod keybinds.
     */
    public static void init() {}
}