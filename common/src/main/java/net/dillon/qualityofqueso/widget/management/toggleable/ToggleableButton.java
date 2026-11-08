package net.dillon.qualityofqueso.widget.management.toggleable;

import net.dillon.qualityofqueso.widget.management.ManagementButton;
import net.dillon.qualityofqueso.widget.management.ManagementParams;
import net.dillon.qualityofqueso.widget.management.ManagementSprites;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.helper.GuiHelper.drawTooltip;
import static net.dillon.qualityofqueso.helper.SoundHelper.playDefaultSound;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * A {@code toggleable} button, which toggles certain management features.
 */
public abstract class ToggleableButton extends ManagementButton {

    protected ToggleableButton(
            ManagementParams params,
            OnPress onPress
    ) {
        super(params, "Toggleable Button", "Redundant", NO_APPENDED_TOOLTIP, false, onPress);
    }

    /**
     * @return The option to toggle.
     */
    protected abstract boolean toggle();

    /**
     * @return the sprite to render for this button.
     */
    protected abstract ManagementSprites getManagementSprites();

    /**
     * @return the base tooltips to render.
     */
    protected abstract Component getDefaultTooltip();

    @Override
    protected boolean canBeActive() {
        return true;
    }

    @Override
    public void playDownSound(SoundManager manager) {
        if (!client().management().playSounds) {
            return;
        }

        playDefaultSound();
    }

    @Override
    protected void extractManagementSprite(GuiGraphicsExtractor graphics) {
        this.drawButtonTexture(graphics, this.getManagementSprites().get(this.toggle()));
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
        this.activateButton();
        this.renderButton(context);

        if (this.isHovered() && client().general().tooltips.enabled()) {
            drawTooltip(this.getDefaultTooltip(), context, mouseX, mouseY);
        }
    }
}