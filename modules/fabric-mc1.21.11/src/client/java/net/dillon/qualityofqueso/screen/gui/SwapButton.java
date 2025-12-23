package net.dillon.qualityofqueso.screen.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

/**
 * A button which swaps inventories.
 */
public class SwapButton extends TransferButton {

    public SwapButton(ScreenHandler screenHandler, TextRenderer textRenderer, String searchFieldText, int x, int y, String buttonName, PressAction onPress, Supplier<Boolean> canBeActive) {
        super(screenHandler, textRenderer, searchFieldText, x, y, buttonName, onPress, canBeActive);
    }

    /**
     * Removes the "with stack" option, since it's a vanilla feature we can't work around (for now).
     */
    @Override
    protected void renderButtonTexture(String id, boolean transferable, ClickableWidget buttonReference, DrawContext
            context) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of("qualityofqueso:textures/gui/" + id + ".png"), buttonReference.getX() - 1, buttonReference.getY() - 1, 0.0F, 0.0F, 12, 12, 12, 12);
    }
}