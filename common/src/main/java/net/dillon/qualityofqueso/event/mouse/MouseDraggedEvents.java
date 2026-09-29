package net.dillon.qualityofqueso.event.mouse;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.qualityofqueso.event.QuesoScreenHolder;
import net.dillon.qualityofqueso.event.management.ManagementEvents;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.inventory.ContainerInput;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.helper.ManagementHelper.hoveredSlotHasItem;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.hasAttemptedToLockSelectOrDeselect;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Handles mouse dragging events.
 */
public class MouseDraggedEvents extends ManagementEvents {

    public MouseDraggedEvents(QuesoScreenHolder screen) {
        super(screen);
    }

    /**
     * Handles drag-sorting, and selecting/locking slots when dragging the mouse.
     */
    public void handleSingularMovingAndLockingOrSelectingSlots(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (isExcludingOrLockingSlots() && hasAttemptedToLockSelectOrDeselect(event)) {
            lockedSlotEvents().selectOrLockSlot(event, cir);
        }

        if (event.button() == InputConstants.MOUSE_BUTTON_LEFT
                && hoveredSlotHasItem(holder().screensHoveredSlot())
                && !transferEvents().canSingularMove()
                && !isExcludingOrLockingSlots()
                && !lockedSlotEvents().isLockedSlot(holder().screensHoveredSlot().index)
                && !isExcludedSlot(holder().screensHoveredSlot().index)
                && (client().isAlwaysQuickMove() || (client().management().dragMoving && event.hasShiftDown()))) {
            sendClickSlotPacket(holder().screensHoveredSlot().index, ContainerInput.QUICK_MOVE);
        }
    }
}