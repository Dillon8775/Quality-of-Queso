package net.dillon.qualityofqueso.helper;

import net.blay09.mods.kuma.api.Kuma;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;

import static net.dillon.qualityofqueso.helper.MethodHelper.kumaAnyModifierDown;
import static net.dillon.qualityofqueso.helper.MethodHelper.kumaMousePressed;
import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;
import static net.dillon.qualityofqueso.keybind.ModKeyMappings.*;

/**
 * A helper class for Quality of Queso's keybinds.
 */
public class ModKeyMappingHelper {

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
        if (QUICK_DROP.getBinding().modifiers().isEmpty()) {
            return true;
        }

        return Kuma.areModifiersActive(QUICK_DROP.getBinding().modifiers());
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
        return (clientOptionsInstance().getManagementOptions().transferring.buttonOrKeyOrKeyOnly() && (kumaAnyModifierDown(MOVE_TO_CONTAINER) || kumaAnyModifierDown(MOVE_TO_INVENTORY)))
                || (clientOptionsInstance().getSortingOptions().sorting.buttonOrKeyOrKeyOnly() && kumaAnyModifierDown(SORT))
                || (clientOptionsInstance().getManagementOptions().quickDrop.buttonOrKeyOrKeyOnly() && hasAllQuickDropModifiersDown())
                || (clientOptionsInstance().getManagementOptions().swapping.buttonOrKeyOrKeyOnly() && kumaAnyModifierDown(SWAP_ITEMS));
    }

    /**
     * @return if the user has the {@code scroll move modifier} key down.
     */
    public static boolean canScrollMoveAndHasScrollModifierDown() {
        return clientOptionsInstance().getManagementOptions().scrollMoving && SCROLL_MOVE.isActiveAndDown();
    }

    /**
     * @return if the user has attempted to lock, unlock, select or deselect a slot.
     */
    public static boolean hasAttemptedToLockSelectOrDeselect(MouseButtonEvent event) {
        return event.button() == 0 || event.button() == 1
                || (clientOptionsInstance().getLockedSlotOptions().lockedSlots && kumaMousePressed(LOCK_SLOT, event));
    }

    /**
     * @return if the lock/unlock slot modifier key is down.
     * <p>
     * Automatically returns {@code true} if the lock slot key has {@code no modifiers}.
     */
    public static boolean hasLockSlotModifierDown() {
        return clientOptionsInstance().getLockedSlotOptions().lockedSlots && kumaAnyModifierDown(LOCK_SLOT);
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
}