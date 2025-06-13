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
    public static final String QUALITY_OF_QUESO = "qualityofqueso.gui.options.title";

    public static final KeyBinding QUICK_EQUIP = KeyBindingHelper.registerKeyBinding(new KeyBinding("qualityofqueso.quick_equip", InputUtil.GLFW_KEY_B, QUALITY_OF_QUESO));

    /**
     * Initialize mod keybinds.
     */
    public static void init() {}
}