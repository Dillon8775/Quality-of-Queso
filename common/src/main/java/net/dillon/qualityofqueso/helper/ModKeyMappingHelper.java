package net.dillon.qualityofqueso.helper;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.kuma.api.Kuma;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;

import static net.dillon.qualityofqueso.helper.MethodHelper.kumaAnyModifierDown;
import static net.dillon.qualityofqueso.helper.MethodHelper.kumaMousePressed;
import static net.dillon.qualityofqueso.keybind.ModKeyMappings.*;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * A helper class for Quality of Queso's keybinds.
 */
public class ModKeyMappingHelper {

    /**
     * @return the user's current drop keybinding.
     */
    public static KeyMapping getDropKey() {
        return Minecraft.getInstance().options.keyDrop;
    }

    /**
     * @return if the user has the quick drop keys held down.
     * <p>
     * Automatically returns {@code true} if quick drop has no modifiers.
     */
    public static boolean hasAllQuickDropModifiersDown() {
        // Return false if unbounded
        if (QUICK_DROP.getBinding().key() == InputConstants.UNKNOWN) {
            return false;
        }

        if (QUICK_DROP.getBinding().modifiers().isEmpty()) {
            return true;
        }

        return Kuma.areModifiersActive(QUICK_DROP.getBinding().modifiers());
    }

    /**
     * @return if the user has the shift key down, to only drop one of each item via quick dropping.
     */
    public static boolean hasDropOnlyOneItemModifierDown() {
        return Minecraft.getInstance().hasShiftDown();
    }

    /**
     * @return if the management modifier key is held down.
     */
    public static boolean hasAnyManagementModifierDown() {
        return (client().management().transferring.any() && (kumaAnyModifierDown(MOVE_TO_CONTAINER) || kumaAnyModifierDown(MOVE_TO_INVENTORY)))
                || (client().sorting().sorting.any() && kumaAnyModifierDown(SORT))
                || (client().management().quickDrop.any() && hasAllQuickDropModifiersDown())
                || (client().management().swapping.any() && kumaAnyModifierDown(SWAP_ITEMS));
    }

    /**
     * @return if the user has the {@code scroll move modifier} key down.
     */
    public static boolean canScrollMoveAndHasScrollModifierDown() {
        return client().management().scrollMoving && Minecraft.getInstance().hasControlDown();
    }

    /**
     * @return if the user has attempted to lock, unlock, select or deselect a slot.
     */
    public static boolean hasAttemptedToLockSelectOrDeselect(MouseButtonEvent event) {
        return event.button() == InputConstants.MOUSE_BUTTON_LEFT || event.button() == InputConstants.MOUSE_BUTTON_RIGHT
                || (client().lockedSlots().lockedSlots && kumaMousePressed(LOCK_SLOT, event));
    }

    /**
     * @return if the lock/unlock slot modifier key is down.
     * <p>
     * Automatically returns {@code true} if the lock slot key has {@code no modifiers}.
     */
    public static boolean hasLockSlotModifierDown() {
        return client().lockedSlots().lockedSlots && kumaAnyModifierDown(LOCK_SLOT);
    }

    /**
     * @return if the user has the keybind to {@code select} slots down, via drag sorting.
     */
    public static boolean hasSelectSlotsModifierDown() {
        return Minecraft.getInstance().hasAltDown();
    }

    /**
     * @return if the user has the keybind to {@code exclude slots} down, via drag sorting.
     */
    public static boolean hasExcludeSlotsModifierDown() {
        return Minecraft.getInstance().hasAltDown() && Minecraft.getInstance().hasShiftDown();
    }
}