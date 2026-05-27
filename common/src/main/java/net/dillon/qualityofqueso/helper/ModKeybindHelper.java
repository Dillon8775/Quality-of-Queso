package net.dillon.qualityofqueso.helper;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import static net.dillon.qualityofqueso.helper.MethodHelper.key;
import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;
import static net.dillon.qualityofqueso.keybind.ModKeybinds.LOCK_SLOT;

/**
 * A helper class for Quality of Queso's keybinds.
 */
public class ModKeybindHelper {

    /**
     * @return the user's current drop keybinding.
     */
    public static KeyMapping getUsersDropKey() {
        return Minecraft.getInstance().options.keyDrop;
    }

    /**
     * @return if the user has the quick drop keys held down.
     * <p>
     * Automatically returns {@code true} if quick drop has no modifiers.
     */
    public static boolean hasAllQuickDropModifiersDown() {
        return Screen.hasControlDown() && Screen.hasAltDown();
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
        return (clientOptionsInstance().getManagementOptions().transferring.buttonOrKeyOrKeyOnly() && Screen.hasControlDown())
                || (clientOptionsInstance().getSortingOptions().sorting.buttonOrKeyOrKeyOnly() && Screen.hasControlDown())
                || (clientOptionsInstance().getManagementOptions().quickDrop.buttonOrKeyOrKeyOnly() && hasAllQuickDropModifiersDown())
                || (clientOptionsInstance().getManagementOptions().swapping.buttonOrKeyOrKeyOnly() && Screen.hasControlDown());
    }

    /**
     * @return if the user has the {@code scroll move modifier} key down.
     */
    public static boolean canScrollMoveAndHasScrollModifierDown() {
        return clientOptionsInstance().getManagementOptions().scrollMoving && Screen.hasControlDown();
    }

    /**
     * @return if the user has attempted to lock, unlock, select or deselect a slot.
     */
    public static boolean hasAttemptedToLockSelectOrDeselect(int bl) {
        return bl == 0 || bl == 1
                || (clientOptionsInstance().getLockedSlotOptions().lockedSlots && bl == key(LOCK_SLOT).getValue());
    }

    /**
     * @return if the lock/unlock slot modifier key is down.
     * <p>
     * Automatically returns {@code true} if the lock slot key has {@code no modifiers}.
     */
    public static boolean hasLockSlotModifierDown() {
        return clientOptionsInstance().getLockedSlotOptions().lockedSlots && Screen.hasAltDown();
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
}