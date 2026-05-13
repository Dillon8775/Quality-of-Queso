package net.dillon.qualityofqueso.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.kuma.api.Kuma;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

import static net.dillon.qualityofqueso.helper.MethodHelper.key;
import static net.dillon.qualityofqueso.helper.ModHelper.options;

/**
 * Keybindings for the {@code Quality of Queso} mod.
 */
public final class ModKeybinds {
    public static final String QOQ_KEY_CATEGORY = "qualityofqueso.title";

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

    /**
     * Initializes Quality of Queso keybinds, using the {@link Kuma} API.
     */
    @Deprecated
    public static void initKeybinds() {
    }

    /**
     * @return if the user has the quick drop keys held down.
     */
    public static boolean hasQuickDropKeysDown() {
        return Screen.hasControlDown() && Screen.hasAltDown();
    }

    /**
     * @return the user's current drop keybinding.
     */
    public static KeyMapping getDropKey() {
        return Minecraft.getInstance().options.keyDrop;
    }

    /**
     * @return if the user has the shift key down, to only drop one of each item via quick dropping.
     */
    public static boolean hasDropOnlyOneItemKeyDown() {
        return Screen.hasShiftDown();
    }

    /**
     * @return if the management modifier key is held down.
     */
    public static boolean hasAnyManagementModifierDown() {
        return (options().management.transferring.buttonOrKeyOrKeyOnly() && Screen.hasControlDown())
                || (options().sorting.sortingEnabled.buttonOrKeyOrKeyOnly() && Screen.hasControlDown())
                || (options().management.quickDrop.buttonOrKeyOrKeyOnly() && hasQuickDropKeysDown())
                || (options().management.swapping.buttonOrKeyOrKeyOnly() && Screen.hasControlDown());
    }

    /**
     * @return if the user has the "move single modifier" key down.
     */
    public static boolean hasMoveSingleModifierDown() {
        return options().management.singularMoving && Screen.hasControlDown();
    }

    /**
     * @return if the user has the keybind to {@code select} slots down, via drag sorting.
     */
    public static boolean hasSelectSlotsKeyDown() {
        return Screen.hasAltDown();
    }

    /**
     * @return if the user has the keybind to {@code exclude slots} down, via drag sorting.
     */
    public static boolean hasExcludeSlotsKeyDown() {
        return Screen.hasAltDown() && Screen.hasShiftDown();
    }

    /**
     * @return if the user has attempted to lock, unlock, select or deselect a slot.
     */
    public static boolean hasClickedToLock(int bl) {
        return (options().lockedSlots.enableLockedSlots && bl == key(LOCK_SLOT).getValue() || bl == 0 || bl == 1);
    }

    /**
     * @return if the lock/unlock slot modifier key is down.
     */
    public static boolean hasLockSlotModifierDown() {
        return options().lockedSlots.enableLockedSlots && Screen.hasAltDown();
    }
}