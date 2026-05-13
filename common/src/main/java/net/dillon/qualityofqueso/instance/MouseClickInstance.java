package net.dillon.qualityofqueso.instance;

import net.dillon.qualityofqueso.instance.management.ManagementInstance;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.MethodHelper.getRecipeBookComponent;
import static net.dillon.qualityofqueso.helper.MethodHelper.getSearchBoxInsideRecipeBook;
import static net.dillon.qualityofqueso.helper.ModHelper.options;
import static net.dillon.qualityofqueso.keybind.ModKeybinds.*;
import static net.dillon.qualityofqueso.util.ModConstants.MOVE_AMOUNT;

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
    public void trySelectingOrLockingSlot(int bl, CallbackInfoReturnable<Boolean> cir) {
        if (isExcludingOrLockingSlots() && hasClickedToLock(bl)) {
            lockedSlotsInstance().selectOrLockSlot(bl, cir);
        }
    }

    /**
     * Quickly equips an item.
     */
    public void quickEquipItem(int bl, CallbackInfoReturnable<Boolean> cir) {
        if (bl == GLFW.GLFW_MOUSE_BUTTON_RIGHT && instance().getScreensHoveredSlot() != null && quickEquipInstance().isQuicklyEquippable(instance().getScreensHoveredSlot().getItem())) {
            quickEquipInstance().quickEquip();
            cir.setReturnValue(true);
        }
    }

    /**
     * Refocus fromInventory search field if clicked.
     */
    public void handleInventorySearchFieldClicking(double mouseX, double mouseY, int bl) {
        if (instance().getSearchFields().inventory() != null && instance().getSearchFields().inventory().mouseClicked(mouseX, mouseY, bl)) {
            instance().getSearchFields().inventory().setFocused(true);
            instance().getScreen().setFocused(instance().getSearchFields().inventory());
            if (instance().getScreen() instanceof InventoryScreen recipeScreen && getSearchBoxInsideRecipeBook(recipeScreen) != null) {
                getSearchBoxInsideRecipeBook(recipeScreen).setFocused(false);
                getRecipeBookComponent(recipeScreen).setFocused(false);
            }
        }
    }

    /**
     * Handles clicking inactive buttons.
     */
    public void handleButtonInactiveSounds(double mouseX, double mouseY) {
        if (buttonHoveredButInactive(instance().getManagementButtons().transferContainer(), mouseX, mouseY)
                || buttonHoveredButInactive(instance().getManagementButtons().transferInventory(), mouseX, mouseY)
                || buttonHoveredButInactive(instance().getManagementButtons().quickDrop(), mouseX, mouseY)
                || buttonHoveredButInactive(instance().getManagementButtons().sort(), mouseX, mouseY)
                || buttonHoveredButInactive(instance().getManagementButtons().swap(), mouseX, mouseY)
        ) {
            playButtonInactiveSound(instance().getMinecraft());
        }
    }

    /**
     * Handles moving only one or dropping one item in a stack.
     */
    public void moveOnlyOne(double mouseX, double mouseY, int bl, CallbackInfoReturnable<Boolean> cir) {
        boolean quickDropping = buttonHoveredAndActive(instance().getManagementButtons().quickDrop(), mouseX, mouseY);
        if (options().management.singularMoving) {
            boolean dropOnlyOne = hasDropOnlyOneItemKeyDown();
            boolean hasSingleModifierDown = hasMoveSingleModifierDown();
            boolean hasKeyDown = quickDropping ? dropOnlyOne : hasSingleModifierDown;
            if (bl == 1 && ((((dropOnlyOne || hasSingleModifierDown) && hoveredSlotHasItem(instance().getScreensHoveredSlot())))
                    || buttonHoveredAndActive(instance().getManagementButtons().transferInventory(), mouseX, mouseY)
                    || buttonHoveredAndActive(instance().getManagementButtons().transferContainer(), mouseX, mouseY))) {
                MOVE_AMOUNT = 1;
                cir.setReturnValue(true);
            } else if (hasKeyDown && transferInstance().tryMoveSingleFromHovered(bl)) {
                cir.setReturnValue(true);
            }
        }
    }
}