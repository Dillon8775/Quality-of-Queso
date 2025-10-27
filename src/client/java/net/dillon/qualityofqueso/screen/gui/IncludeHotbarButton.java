package net.dillon.qualityofqueso.screen.gui;

import net.dillon.qualityofqueso.util.ButtonUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

import static net.dillon.qualityofqueso.main.QoQ.options;

/**
 * A separate class for the include hotbar button.
 */
public class IncludeHotbarButton extends TransferButton {

    public IncludeHotbarButton(ScreenHandler screenHandler, TextRenderer textRenderer, String searchFieldText, int x, int y, String buttonName, PressAction onPress) {
        super(screenHandler, textRenderer, searchFieldText, x, y, buttonName, onPress);
    }

    /**
     * Renders the textures and tooltips for the button.
     */
    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (this.isHovered()) {
            this.renderButtonTexture(options().includeHotbar ? "include_hotbar_button_hovered" : "exclude_hotbar_button_hovered", false, this, context);
            ButtonUtil.drawTooltip(options().includeHotbar ?
                    Text.translatable("qualityofqueso.gui.include_hotbar") :
                    Text.translatable("qualityofqueso.gui.exclude_hotbar"), context, this.textRenderer, mouseX, mouseY);
            this.renderCursor(context);
        } else {
            this.renderButtonTexture(options().includeHotbar ? "include_hotbar_button" : "exclude_hotbar_button", false, this, context);
        }
    }
}