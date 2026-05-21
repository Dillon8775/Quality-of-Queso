package net.dillon.qualityofqueso.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static net.dillon.qualityofqueso.helper.GuiHelper.drawTooltip;
import static net.dillon.qualityofqueso.helper.ManagementHelper.playDefaultSound;
import static net.dillon.qualityofqueso.helper.ModHelper.options;

/**
 * A class for the include hotbar button and transportables button.
 */
public abstract class ToggleableButton extends TransferButton {

    public ToggleableButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, String buttonName, OnPress onPress) {
        super(screenHandler, font, searchFieldText, null, buttonName, false, onPress);
    }

    /**
     * @return the toggled on texture to use.
     */
    protected abstract String onTextureId();

    /**
     * @return the toggled off texture to use.
     */
    protected abstract String offTextureId();

    /**
     * @return The option to go off of.
     */
    protected abstract boolean option();

    /**
     * @return the base tooltip to render.
     */
    @Override
    protected abstract Component getTooltipToRender();

    @Override
    public void playDownSound(SoundManager manager) {
        if (!options().management.playSounds) {
            return;
        }

        playDefaultSound(manager);
    }

    /**
     * Renders the textures and tooltips for the button.
     */
    @Override
    protected void renderContents(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        this.activateButton();
        this.renderBaseButtonTexture(this.option() ? this.onTextureId() : this.offTextureId(), this, context);
        if (this.isHovered() && options().accessibility.tooltips.on()) {
            drawTooltip(this.getTooltipToRender(), context, this.font, mouseX, mouseY);
        }
    }
}