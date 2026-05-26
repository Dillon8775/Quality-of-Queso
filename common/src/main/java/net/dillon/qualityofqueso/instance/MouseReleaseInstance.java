package net.dillon.qualityofqueso.instance;

import net.dillon.qualityofqueso.instance.management.ManagementInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.helper.MethodHelper.key;
import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;
import static net.dillon.qualityofqueso.helper.ModKeybindHelper.hasAttemptedToLockSelectOrDeselect;
import static net.dillon.qualityofqueso.keybind.ModKeybinds.LOCK_SLOT;

/**
 * Handles mouse-releasing functions.
 */
public class MouseReleaseInstance extends ManagementInstance {

    public MouseReleaseInstance(QuesoScreen screen) {
        super(screen);
    }

    /**
     * Disables hard-locked slots from being affected by double clicking.
     */
    public void disableHardLockedSlotsOnDoubleClick(Slot slot, Slot target, CallbackInfoReturnable<Boolean> cir) {
        // If hovered slot is excluded or locked, or if the matching target slot is excluded or locked, do not move it when double-clicking quick move
        if (isExcludedSlot(slot.index)
                || isExcludedSlot(target.index)
                || lockedSlotsInstance().isLockedSlot(target.index)
                || lockedSlotsInstance().isLockedSlot(slot.index)) {
            cir.cancel();
        }
    }

    /**
     * Tracks locked slots, and selects/locks them.
     */
    public void trackSlotAndLockOrSelect(int bl, CallbackInfoReturnable<Boolean> cir) {
        if (isExcludingOrLockingSlots() && hasAttemptedToLockSelectOrDeselect(bl)) {
            lockedSlotsInstance().selectOrLockSlot(bl, cir);
        }

        if (clientOptionsInstance().getLockedSlotOptions().lockedSlots && bl == key(LOCK_SLOT).getValue() && Screen.hasAltDown()) {
            instance().setLastLockedSlotIndex(-1);
            instance().setLockDragAction(0);
        }
    }
}