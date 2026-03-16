package net.dillon.qualityofqueso.screen.gui.button;

import net.dillon.qualityofqueso.util.ButtonUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.function.Supplier;

import static net.dillon.qualityofqueso.util.ModUtil.ofQoQ;

/**
 * Representation of the quick drop button.
 */
public class QuickDropButton extends TransferButton {

    public QuickDropButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, int x, int y, String buttonName, OnPress onPress, Supplier<Boolean> canBeActive) {
        super(screenHandler, font, searchFieldText, x, y, buttonName, true, onPress, canBeActive);
    }

    /**
     * Removes the "with stack" option, since it's a vanilla feature we can't work around (for now).
     */
    @Override
    protected void renderButtonTexture(String id, AbstractWidget buttonReference, GuiGraphicsExtractor context) {
        String transferableString = this.searchFieldText.startsWith("!") ?
                "_exclude.png" : this.searchFieldText.startsWith("#") ?
                "_with_tag.png" : this.searchFieldText.startsWith(":") ?
                "_match.png" : ".png";
        context.blit(RenderPipelines.GUI_TEXTURED, ofQoQ("textures/gui/button/" + id + transferableString), buttonReference.getX() - 1, buttonReference.getY() - 1, 0.0F, 0.0F, 12, 12, 12, 12);
        boolean shortcutKeyPressed = Minecraft.getInstance().hasControlDown() && Minecraft.getInstance().hasAltDown();
        if (shortcutKeyPressed) {
            ButtonUtil.drawButtonTexture(context, "outline/quick_drop_outline", this);
        }
        if (Minecraft.getInstance().hasShiftDown() && (this.isHovered() || shortcutKeyPressed)) {
            ButtonUtil.drawButtonTexture(context, "shortcut/quick_drop_one", this);
        }
    }
}