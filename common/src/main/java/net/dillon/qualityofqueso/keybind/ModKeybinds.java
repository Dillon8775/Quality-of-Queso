package net.dillon.qualityofqueso.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.kuma.api.Kuma;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

/**
 * Keybindings for the {@code Quality of Queso} mod.
 */
public final class ModKeybinds {
    public static final String QOQ_KEY_CATEGORY = "qualityofqueso.title";

    /**
     * Initializes Quality of Queso keybinds, using the {@link Kuma} API.
     */
    @Deprecated
    public static void initKeybinds() {
    }

    public static final KeyMapping LOCK_SLOT = new KeyMapping(
            "key.qualityofqueso.lock_slot",
            InputConstants.Type.MOUSE,
            InputConstants.MOUSE_BUTTON_MIDDLE,
            QOQ_KEY_CATEGORY
    );

    public static final KeyMapping MOVE_TO_CONTAINER = new KeyMapping(
            "key.qualityofqueso.move_to_container",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            QOQ_KEY_CATEGORY
    );

    public static final KeyMapping MOVE_TO_INVENTORY = new KeyMapping(
            "key.qualityofqueso.move_to_inventory",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            QOQ_KEY_CATEGORY
    );

    public static final KeyMapping OPEN_SEARCH_ITEM_FRAMES_GUI = new KeyMapping(
            "key.qualityofqueso.open_item_frame_search_gui",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            QOQ_KEY_CATEGORY
    );

    public static final KeyMapping OPEN_QUALITY_OF_QUESO_MAIN_MENU = new KeyMapping(
            "key.qualityofqueso.open_quality_of_queso_main_menu",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            QOQ_KEY_CATEGORY
    );

    public static final KeyMapping OPEN_VISUAL_TIME_GUI = new KeyMapping(
            "key.qualityofqueso.open_visual_time_gui",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            QOQ_KEY_CATEGORY
    );

    public static final KeyMapping QUICK_EQUIP = new KeyMapping(
            "key.qualityofqueso.quick_equip",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            QOQ_KEY_CATEGORY
    );

    public static final KeyMapping SORT = new KeyMapping(
            "key.qualityofqueso.sort",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_S,
            QOQ_KEY_CATEGORY
    );

    public static final KeyMapping SWAP_ITEMS = new KeyMapping(
            "key.qualityofqueso.swap_items",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            QOQ_KEY_CATEGORY
    );

    public static final KeyMapping VIEW_LAST_KNOWN_ENDER_CHEST = new KeyMapping(
            "key.qualityofqueso.view_last_known_ender_chest",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            QOQ_KEY_CATEGORY
    );
}