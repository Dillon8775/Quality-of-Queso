package net.dillon.qualityofqueso.instance;

import net.dillon.qualityofqueso.instance.management.ManagementInstance;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.inventory.ClickType;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.helper.ManagementHelper.hoveredSlotHasItem;
import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.hasAttemptedToLockSelectOrDeselect;

/**
 * Handles mouse dragging events.
 */
public class MouseDragInstance extends ManagementInstance {

    public MouseDragInstance(QuesoScreen screen) {
        super(screen);
    }

    /**
     * Handles drag-sorting, and selecting/locking slots when dragging the mouse.
     */
    public void handleSingularMovingAndLockingOrSelectingSlots(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (isExcludingOrLockingSlots() && hasAttemptedToLockSelectOrDeselect(event)) {
            lockedSlotsInstance().selectOrLockSlot(event, cir);
        }

        if (event.button() == 0
                && hoveredSlotHasItem(instance().getScreensHoveredSlot())
                && !transferInstance().canSingularMove()
                && !isExcludingOrLockingSlots()
                && !lockedSlotsInstance().isLockedSlot(instance().getScreensHoveredSlot().index)
                && !isExcludedSlot(instance().getScreensHoveredSlot().index)
                && (clientOptionsInstance().isAlwaysQuickMove() || (clientOptionsInstance().getManagementOptions().dragMoving && event.hasShiftDown()))) {
            sendClickSlotPacket(instance().getScreensHoveredSlot().index, ClickType.QUICK_MOVE);
        }
    }
}