package net.dillon.qualityofqueso.widget;

import net.dillon.qualityofqueso.helper.ButtonHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.function.Supplier;

import static net.dillon.qualityofqueso.helper.ManagementHelper.isInventoryScreen;
import static net.dillon.qualityofqueso.helper.MethodHelper.getHoveredSlot;
import static net.dillon.qualityofqueso.helper.ModHelper.*;
import static net.dillon.qualityofqueso.keybind.ModKeybinds.hasDropOnlyOneItemKeyDown;
import static net.dillon.qualityofqueso.keybind.ModKeybinds.hasQuickDropKeysDown;
import static net.dillon.qualityofqueso.util.ModConstants.MOVE_ONE_PATH;

/**
 * Representation of the quick drop button.
 */
public class QuickDropButton extends TransferButton {

    public QuickDropButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, String resourceLocation, String buttonName, OnPress onPress, Supplier<Boolean> canBeActive) {
        super(screenHandler, font, searchFieldText, resourceLocation, buttonName, true, onPress, canBeActive);
    }

    @Override
    protected String getAppendedTooltip() {
        return isInventoryScreen(getCurrentScreen()) ? ".inventory" : super.getAppendedTooltip();
    }

    /**
     * Removes the "with stack" option, since it's a vanilla feature we can't work around (for now).
     */
    @Override
    protected void renderBaseButtonTexture(String id, AbstractWidget buttonReference, GuiGraphics graphics, double mouseX, double mouseY) {
        String transferableString = this.searchFieldText.startsWith("!") ?
                "_excluding.png" : this.searchFieldText.startsWith("#") ?
                                   "_tag.png" : this.searchFieldText.startsWith(":") ?
                                                "_matching.png" : ".png";
        if (!this.canBeActive.get()) {
            transferableString = ".png";
        }
        this.renderBaseTexture(graphics);
        graphics.blit(ofQoQ("textures/gui/button/" + id + transferableString), buttonReference.getX() - 1, buttonReference.getY() - 1, 0.0F, 0.0F, 12, 12, 12, 12);
        this.renderHoveredTexture(graphics, mouseX, mouseY);
        boolean shortcutKeyPressed = hasQuickDropKeysDown();
        if (shortcutKeyPressed) {
            String outline = "quick_drop_all";
            if (getCurrentScreen() instanceof AbstractContainerScreen<?> screen && getHoveredSlot(screen) != null && getHoveredSlot(screen).hasItem()) {
                outline = "quick_drop_hovered";
            }
            ButtonHelper.drawButtonTexture(graphics, "quick_drop/" + outline, this);
        }
        if (hasDropOnlyOneItemKeyDown()
                && ((options().management.singularMoving && ((this.isMouseOver(mouseX, mouseY)) || getCurrentScreen() instanceof AbstractContainerScreen<?> screen && getHoveredSlot(screen) != null && getHoveredSlot(screen).hasItem())) || shortcutKeyPressed)) {
            ButtonHelper.drawButtonTexture(graphics, MOVE_ONE_PATH, this);
        }
    }
}