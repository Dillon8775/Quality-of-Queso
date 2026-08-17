package net.dillon.qualityofqueso.instance;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.qualityofqueso.instance.management.ManagementInstance;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.ModConstants.MOVE_AMOUNT;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.*;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * Handles mouse-clicking events.
 */
public class MouseClickInstance extends ManagementInstance {

    public MouseClickInstance(QuesoScreen screen) {
        super(screen);
    }

    /**
     * Attempts to lock or select a slot when clicking.
     */
    public void trySelectingOrLockingSlot(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (isExcludingOrLockingSlots() && hasAttemptedToLockSelectOrDeselect(event)) {
            lockedSlotsInstance().selectOrLockSlot(event, cir);
        }
    }

    /**
     * Quickly equips an item.
     */
    public void quickEquipItem(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT && instance().getScreensHoveredSlot() != null && quickEquipInstance().isQuicklyEquippable(instance().getScreensHoveredSlot().getItem())) {
            quickEquipInstance().quickEquipItem();
            cir.setReturnValue(true);
        }
    }

    /**
     * Refocus fromInventory search field if clicked.
     */
    public void handleInventorySearchFieldClicking(MouseButtonEvent event, boolean doubleClick) {
        if (instance().getSearchFields().inventory() != null && instance().getSearchFields().inventory().mouseClicked(event, doubleClick)) {
            instance().getSearchFields().inventory().setFocused(true);
        }
    }

    /**
     * Handles clicking inactive buttons.
     */
    public void handleButtonInactiveSounds() {
        if (buttonHoveredButInactive(instance().getManagementButtons().transferContainer())
                || buttonHoveredButInactive(instance().getManagementButtons().transferInventory())
                || buttonHoveredButInactive(instance().getManagementButtons().quickDrop())
                || buttonHoveredButInactive(instance().getManagementButtons().sort())
                || buttonHoveredButInactive(instance().getManagementButtons().swap())
        ) {
            playButtonInactiveSound(instance().getMinecraft());
        }
    }

    /**
     * Handles moving only one or dropping one item in a stack.
     */
    public void moveOnlyOne(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (client().management().scrollMoving) {
            boolean dropOnlyOne = hasDropOnlyOneItemModifierDown();
            boolean hasSingleModifierDown = canScrollMoveAndHasScrollModifierDown();
            if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT && ((((dropOnlyOne || hasSingleModifierDown) && hoveredSlotHasItem(instance().getScreensHoveredSlot())))
                    || buttonHoveredAndActive(instance().getManagementButtons().transferInventory())
                    || buttonHoveredAndActive(instance().getManagementButtons().transferContainer()))) {
                MOVE_AMOUNT = 1;
                cir.setReturnValue(true);
            }
        }
    }
}