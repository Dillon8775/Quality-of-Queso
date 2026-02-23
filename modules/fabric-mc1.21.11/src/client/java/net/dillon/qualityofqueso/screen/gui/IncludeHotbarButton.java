package net.dillon.qualityofqueso.screen.gui;

import net.dillon.qualityofqueso.util.ButtonUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundEvents;

import static net.dillon.qualityofqueso.main.QoQ.options;
import static net.minecraft.text.Text.translatable;

/**
 * A separate class for the include hotbar button.
 */
public class IncludeHotbarButton extends TransferButton {

    public IncludeHotbarButton(ScreenHandler screenHandler, TextRenderer textRenderer, String searchFieldText, int x, int y, String buttonName, PressAction onPress) {
        super(screenHandler, textRenderer, searchFieldText, x, y, buttonName, onPress);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    /**
     * Renders the textures and tooltips for the button.
     */
    @Override
    protected void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (this.isHovered()) {
            this.renderButtonTexture(options().includeHotbar ? "include_hotbar_button_hovered" : "exclude_hotbar_button_hovered", false, this, context);
            ButtonUtil.drawTooltip(options().includeHotbar ?
                    translatable("qualityofqueso.gui.include_hotbar") :
                    translatable("qualityofqueso.gui.exclude_hotbar"), context, this.textRenderer, mouseX, mouseY);
        } else {
            this.renderButtonTexture(options().includeHotbar ? "include_hotbar_button" : "exclude_hotbar_button", false, this, context);
        }
    }
}