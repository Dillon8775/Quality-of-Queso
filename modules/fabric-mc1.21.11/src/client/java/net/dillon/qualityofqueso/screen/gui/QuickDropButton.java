package net.dillon.qualityofqueso.screen.gui;

import net.dillon.qualityofqueso.util.ButtonUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

/**
 * Representation of the quick drop button.
 */
public class QuickDropButton extends TransferButton {

    public QuickDropButton(ScreenHandler screenHandler, TextRenderer textRenderer, String searchFieldText, int x, int y, String buttonName, PressAction onPress, Supplier<Boolean> canBeActive) {
        super(screenHandler, textRenderer, searchFieldText, x, y, buttonName, onPress, canBeActive);
    }

    /**
     * Removes the "with stack" option, since it's a vanilla feature we can't work around (for now).
     */
    @Override
    protected void renderButtonTexture(String id, boolean transferable, ClickableWidget buttonReference, DrawContext
            context) {
        String transferableString = this.searchFieldText.startsWith("!") ?
                "_exclude.png" : this.searchFieldText.startsWith("#") ?
                "_with_tag.png" : this.searchFieldText.startsWith(":") ?
                "_match.png" : ".png";
        String appended = transferable ? transferableString : ".png";
        context.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of("qualityofqueso:textures/gui/" + id + appended), buttonReference.getX() - 1, buttonReference.getY() - 1, 0.0F, 0.0F, 12, 12, 12, 12);
        boolean shortcutKeyPressed = MinecraftClient.getInstance().isCtrlPressed() && MinecraftClient.getInstance().isAltPressed();
        if (shortcutKeyPressed) {
            ButtonUtil.drawButtonTexture(context, "quick_drop_outline", this);
        }
        if (MinecraftClient.getInstance().isShiftPressed() && (this.isHovered() || shortcutKeyPressed)) {
            ButtonUtil.drawButtonTexture(context, "quick_drop_one", this);
        }
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        soundManager.play(PositionedSoundInstance.master(SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, 1.0F, 5.0F));
    }
}