package net.dillon.qualityofqueso.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import static net.dillon.qualityofqueso.util.ModUtil.ofQoQ;

/**
 * Keybindings for the {@code Quality of Queso} mod.
 */
public class ModKeybinds {
    private static final KeyMapping.Category QOQ = KeyMapping.Category.register(ofQoQ("quality_of_queso"));

    public static final KeyMapping QUICK_EQUIP = new KeyMapping(
            "qualityofqueso.quick_equip",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            QOQ
    );

    public static final KeyMapping MOVE_CONTAINER = new KeyMapping(
            "qualityofqueso.move_container",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            QOQ
    );

    public static final KeyMapping MOVE_INVENTORY = new KeyMapping(
            "qualityofqueso.move_inventory",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            QOQ
    );

    public static final KeyMapping SORT_CONTAINER = new KeyMapping(
            "qualityofqueso.sort_container",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_S,
            QOQ
    );

    public static final KeyMapping SWAP_ITEMS = new KeyMapping(
            "qualityofqueso.swap_items",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            QOQ
    );

    public static final KeyMapping OPEN_SEARCH_ITEM_FRAMES_GUI = new KeyMapping(
            "qualityofqueso.open_item_frame_search_gui",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            QOQ,
            1
    );

    public static final KeyMapping HIDE_RECIPE_BOOK = new KeyMapping(
            "qualityofqueso.hide_recipe_book",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            QOQ
    );
}