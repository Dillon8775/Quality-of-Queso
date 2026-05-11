package net.dillon.qualityofqueso.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.kuma.api.*;
import net.dillon.qualityofqueso.screen.EnderChestPreviewScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;

import static net.dillon.qualityofqueso.helper.MethodHelper.kumaAnyModifierDown;
import static net.dillon.qualityofqueso.helper.MethodHelper.kumaMousePressed;
import static net.dillon.qualityofqueso.helper.ModHelper.*;

/**
 * Keybindings for the {@code Quality of Queso} mod.
 */
public final class ModKeyMappings {
    public static final KeyMapping.Category QOQ_KEY_CATEGORY = KeyMapping.Category.register(ofQoQ("quality_of_queso"));

    public static final ManagedKeyMapping LOCK_SLOT = Kuma.createKeyMapping(ofQoQ("lock_slot"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .withDefault(InputBinding.mouse(InputConstants.MOUSE_BUTTON_MIDDLE, KeyModifiers.of(KeyModifier.ALT)))
            .build();

    public static final ManagedKeyMapping MOVE_SINGLE_ITEM = Kuma.createKeyMapping(ofQoQ("move_single"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .withDefault(InputBinding.key(InputConstants.KEY_LCONTROL))
            .build();

    public static final ManagedKeyMapping MOVE_TO_CONTAINER = Kuma.createKeyMapping(ofQoQ("move_to_container"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .withDefault(InputBinding.key(InputConstants.KEY_C, KeyModifiers.of(KeyModifier.CONTROL)))
            .build();

    public static final ManagedKeyMapping MOVE_TO_INVENTORY = Kuma.createKeyMapping(ofQoQ("move_to_inventory"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .withDefault(InputBinding.key(InputConstants.KEY_I, KeyModifiers.of(KeyModifier.CONTROL)))
            .build();

    public static final ManagedKeyMapping OPEN_SEARCH_ITEM_FRAMES_GUI = Kuma.createKeyMapping(ofQoQ("open_item_frame_search_gui"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .withDefault(InputBinding.key(InputConstants.KEY_I))
            .handleWorldInput(event -> false)
            .build();

    public static final ManagedKeyMapping QUICK_DROP = Kuma.createKeyMapping(ofQoQ("quick_drop"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .withDefault(InputBinding.key(InputConstants.KEY_Q, KeyModifiers.of(KeyModifier.CONTROL, KeyModifier.ALT)))
            .build();

    public static final ManagedKeyMapping QUICK_EQUIP = Kuma.createKeyMapping(ofQoQ("quick_equip"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .build();

    public static final ManagedKeyMapping SORT = Kuma.createKeyMapping(ofQoQ("sort"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .withDefault(InputBinding.key(InputConstants.KEY_S, KeyModifiers.of(KeyModifier.CONTROL)))
            .build();

    public static final ManagedKeyMapping SWAP_ITEMS = Kuma.createKeyMapping(ofQoQ("swap_items"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .build();

    public static final ManagedKeyMapping VIEW_LAST_KNOWN_ENDER_CHEST = Kuma.createKeyMapping(
                    ofQoQ("view_last_known_ender_chest"))
            .overrideCategory(QOQ_KEY_CATEGORY)
            .withDefault(InputBinding.key(InputConstants.KEY_N, KeyModifiers.of(KeyModifier.CONTROL)))
            .handleWorldInput(event -> {
                Minecraft minecraft =  Minecraft.getInstance();
                if (modEnabled(minecraft) && minecraft.level != null && minecraft.player != null) {
                    Minecraft.getInstance().setScreen(new EnderChestPreviewScreen());
                    return true;
                }
                return false;
            })
            .build();

    /**
     * Initializes Quality of Queso keybinds, using the {@link Kuma} API.
     */
    public static void initKeybinds() {
    }

    /**
     * @return if the user has the quick drop keys held down.
     */
    public static boolean hasQuickDropKeysDown() {
        return Kuma.areModifiersActive(QUICK_DROP.getBinding().modifiers());
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
        return Minecraft.getInstance().hasShiftDown();
    }

    /**
     * @return if the management modifier key is held down.
     */
    public static boolean hasAnyManagementModifierDown() {
        return (options().management.transferring.buttonOrKeyOrKeyOnly() && (kumaAnyModifierDown(MOVE_TO_CONTAINER) || kumaAnyModifierDown(MOVE_TO_INVENTORY)))
                || (options().sorting.sortingEnabled.buttonOrKeyOrKeyOnly() && kumaAnyModifierDown(SORT))
                || (options().management.quickDrop.buttonOrKeyOrKeyOnly() && hasQuickDropKeysDown())
                || (options().management.swapping.buttonOrKeyOrKeyOnly() && kumaAnyModifierDown(SWAP_ITEMS));
    }

    /**
     * @return if the user has the "move single modifier" key down.
     */
    public static boolean hasMoveSingleModifierDown() {
        return options().management.singularMoving && MOVE_SINGLE_ITEM.isActiveAndDown();
    }

    /**
     * @return if the user has the keybind to {@code select} slots down, via drag sorting.
     */
    public static boolean hasSelectSlotsKeyDown() {
        return Minecraft.getInstance().hasAltDown();
    }

    /**
     * @return if the user has the keybind to {@code exclude slots} down, via drag sorting.
     */
    public static boolean hasExcludeSlotsKeyDown() {
        return Minecraft.getInstance().hasAltDown() && Minecraft.getInstance().hasShiftDown();
    }

    /**
     * @return if the user has attempted to lock, unlock, select or deselect a slot.
     */
    public static boolean hasClickedToLock(MouseButtonEvent event) {
        return (options().lockedSlots.enableLockedSlots && kumaMousePressed(LOCK_SLOT, event)) || event.button() == 0 || event.button() == 1;
    }

    /**
     * @return if the lock/unlock slot modifier key is down.
     */
    public static boolean hasLockSlotModifierDown() {
        return options().lockedSlots.enableLockedSlots && kumaAnyModifierDown(LOCK_SLOT);
    }
}