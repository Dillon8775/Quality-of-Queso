package net.dillon.qualityofqueso.widget.management;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import static net.dillon.dillonlib.task.ClientTasks.getLocalPlayer;
import static net.dillon.dillonlib.task.ClientTasks.getScreen;
import static net.dillon.qualityofqueso.helper.ManagementHelper.*;
import static net.dillon.qualityofqueso.helper.MethodHelper.getHoveredSlot;
import static net.dillon.qualityofqueso.helper.ModConstants.MOVE_ONE_PATH;
import static net.dillon.qualityofqueso.helper.ModConstants.QUICK_DROP_BUTTON_NAME;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.hasAllQuickDropModifiersDown;
import static net.dillon.qualityofqueso.helper.ModKeyMappingHelper.hasDropOnlyOneItemModifierDown;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.widget.management.ManagementSprites.managementIdentifier;

/**
 * A management button that quickly drops all items in a container/player's inventory.
 */
public class QuickDropButton extends ManagementButton {

    public QuickDropButton(ManagementParams params) {
        super(
                params,
                QUICK_DROP_BUTTON_NAME,
                isInventoryScreen() ? ".inventory" : NO_APPENDED_TOOLTIP,
                true,
                b -> params.managementEvents().transferEvents().dropItems(!isContainerScreen())
        );
    }

    @Override
    protected boolean canBeActive() {
        boolean containerScreen = isContainerScreen();
        return (isInventoryScreen() ?
                managementEvents.isAnySlotFilled(true, 9, 36) :
                managementEvents.isAnySlotFilled(false, 0, managementEvents.getContainerSize()))
                && managementEvents.getCursorStack().isEmpty()
                && managementEvents.shouldButtonBeActive(!containerScreen, containerScreen ? null : getLocalPlayer().getInventory());
    }

    @Override
    protected String getDefaultAppendedTexture() {
        String appended = "";

        if (isExcluding(searchFieldText)) {
            appended = "_excluding";
        } else if (isTagFiltering(searchFieldText)) {
            appended = "_tag";
        } else if (isMatchCase(searchFieldText)) {
            appended = "_matching";
        }

        return !this.canBeActive() ? "" : appended;
    }

    @Override
    protected void renderButton(GuiGraphicsExtractor graphics) {
        this.extractButton(graphics);

        boolean shortcutKeyPressed = hasAllQuickDropModifiersDown();
        if (shortcutKeyPressed) {

            String outline = "quick_drop_all";

            if (getScreen() instanceof AbstractContainerScreen<?> screen && getHoveredSlot(screen) != null && getHoveredSlot(screen).hasItem()) {
                outline = "quick_drop_hovered";
            }

            this.drawButtonTexture(graphics, managementIdentifier(buttonName + "/" + outline));
        }

        if (hasDropOnlyOneItemModifierDown()
                && (
                        (
                                client().management().scrollMoving && (
                                        (this.isHovered()) || getScreen() instanceof AbstractContainerScreen<?> screen && getHoveredSlot(screen) != null && getHoveredSlot(screen).hasItem()
                                )
                        ) || shortcutKeyPressed
        )) {
            this.drawButtonTexture(graphics, managementIdentifier(MOVE_ONE_PATH));
        }
    }
}