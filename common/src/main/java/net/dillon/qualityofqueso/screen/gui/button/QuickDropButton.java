package net.dillon.qualityofqueso.screen.gui.button;

import net.dillon.qualityofqueso.util.ButtonUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.function.Supplier;

import static net.dillon.qualityofqueso.util.ButtonUtil.isInventoryScreen;
import static net.dillon.qualityofqueso.util.ModUtil.ofQoQ;

/**
 * Representation of the quick drop button.
 */
public class QuickDropButton extends TransferButton {

    public QuickDropButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, String resourceLocation, String buttonName, OnPress onPress, Supplier<Boolean> canBeActive) {
        super(screenHandler, font, searchFieldText, resourceLocation, buttonName, true, onPress, canBeActive);
    }

    @Override
    protected String getAppendedTooltip() {
        return isInventoryScreen(Minecraft.getInstance().screen) ? ".inventory" : super.getAppendedTooltip();
    }

    /**
     * Removes the "with stack" option, since it's a vanilla feature we can't work around (for now).
     */
    @Override
    protected void renderBaseButtonTexture(String id, AbstractWidget buttonReference, GuiGraphicsExtractor graphics) {
        String transferableString = this.searchFieldText.startsWith("!") ?
                "_excluding.png" : this.searchFieldText.startsWith("#") ?
                "_tag.png" : this.searchFieldText.startsWith(":") ?
                "_matching.png" : ".png";
        this.renderBaseTexture(graphics);
        graphics.blit(RenderPipelines.GUI_TEXTURED, ofQoQ("textures/gui/button/" + id + transferableString), buttonReference.getX() - 1, buttonReference.getY() - 1, 0.0F, 0.0F, 12, 12, 12, 12);
        this.renderHoveredTexture(graphics);
        boolean shortcutKeyPressed = Minecraft.getInstance().hasControlDown() && Minecraft.getInstance().hasAltDown();
        if (shortcutKeyPressed) {
            ButtonUtil.drawButtonTexture(graphics, "outline/quick_drop_outline", this);
        }
        if (Minecraft.getInstance().hasShiftDown() && (this.isHovered() || shortcutKeyPressed)) {
            ButtonUtil.drawButtonTexture(graphics, "shortcut/quick_drop_one", this);
        }
    }
}