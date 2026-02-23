package net.dillon.qualityofqueso.screen.gui;

import net.dillon.qualityofqueso.util.ButtonUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static net.dillon.qualityofqueso.main.QoQ.options;

/**
 * A separate class for the include hotbar button.
 */
public class IncludeHotbarButton extends TransferButton {

    public IncludeHotbarButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, int x, int y, String buttonName, OnPress onPress) {
        super(screenHandler, font, searchFieldText, x, y, buttonName, onPress);
    }

    /**
     * Renders the textures and tooltips for the button.
     */
    @Override
    protected void renderContents(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        if (this.isHovered()) {
            this.renderButtonTexture(options().includeHotbar ? "include_hotbar_button_hovered" : "exclude_hotbar_button_hovered", false, this, context);
            ButtonUtil.drawTooltip(options().includeHotbar ?
                    Component.translatable("qualityofqueso.gui.include_hotbar") :
                    Component.translatable("qualityofqueso.gui.exclude_hotbar"), context, this.font, mouseX, mouseY);
        } else {
            this.renderButtonTexture(options().includeHotbar ? "include_hotbar_button" : "exclude_hotbar_button", false, this, context);
        }
    }

    @Override
    public void playDownSound(SoundManager manager) {
        manager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}