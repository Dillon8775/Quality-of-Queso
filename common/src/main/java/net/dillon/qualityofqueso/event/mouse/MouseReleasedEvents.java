package net.dillon.qualityofqueso.event.mouse;

import net.dillon.qualityofqueso.event.QuesoScreenHolder;
import net.dillon.qualityofqueso.event.management.ManagementEvents;
import net.dillon.qualityofqueso.keybind.ModKeyMappings;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.helper.MethodHelper.kumaMousePressed;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.hasAttemptedToLockSelectOrDeselect;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Handles mouse-releasing functions.
 */
public class MouseReleasedEvents extends ManagementEvents {

    public MouseReleasedEvents(QuesoScreenHolder screen) {
        super(screen);
    }

    /**
     * Disables hard-locked slots from being affected by double clicking.
     */
    public void disableHardLockedSlotsOnDoubleClick(Slot slot, Slot target, CallbackInfoReturnable<Boolean> cir) {
        // If hovered slot is excluded or locked, or if the matching target slot is excluded or locked, do not move it when double-clicking quick move
        if (isExcludedSlot(slot.index)
                || isExcludedSlot(target.index)
                || lockedSlotEvents().isLockedSlot(target.index)
                || lockedSlotEvents().isLockedSlot(slot.index)) {
            cir.cancel();
        }
    }

    /**
     * Tracks locked slots, and selects/locks them.
     */
    public void trackSlotAndLockOrSelect(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (isExcludingOrLockingSlots() && hasAttemptedToLockSelectOrDeselect(event)) {
            lockedSlotEvents().selectOrLockSlot(event, cir);
        }

        if (client().lockedSlots().lockedSlots && kumaMousePressed(ModKeyMappings.LOCK_SLOT, event)) {
            holder().setLastLockedSlotIndex(-1);
            holder().setLockDragAction(0);
        }
    }
}