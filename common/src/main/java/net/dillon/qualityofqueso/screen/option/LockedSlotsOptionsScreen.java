package net.dillon.qualityofqueso.screen.option;

import net.dillon.qualityofqueso.helper.ModHelper;
import net.dillon.qualityofqueso.widget.gui.ColorField;
import net.dillon.qualityofqueso.widget.gui.LockedSlotColorField;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.helper.GuiHelper.drawTooltip;
import static net.dillon.qualityofqueso.helper.ModHelper.ofQoQ;
import static net.dillon.qualityofqueso.util.ModConstants.DEFAULT_LOCKED_SLOT_COLOR;
import static net.dillon.qualityofqueso.util.ModConstants.LOCKED_TEXTURE;

public class LockedSlotsOptionsScreen extends AbstractModOptionsScreen {
    private AbstractWidget showLock, hardLockSlots, lockSound, preventDropping;
    private LockedSlotColorField lockedSlotTextColorField;

    public LockedSlotsOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.locked_slots"));
    }

    @Override
    protected AbstractWidget[] options() {
        this.lockedSlotTextColorField = new LockedSlotColorField(
                this.font,
                ModHelper.options().lockedSlots.lockedSlotColor,
                DEFAULT_LOCKED_SLOT_COLOR,
                Component.translatable("qualityofqueso.options.locked_slot_color.tooltip")
        );

        this.showLock = createOption(ListOptions.showLock());
        this.hardLockSlots = createOption(ListOptions.hardLockSlots());
        this.lockSound = createOption(ListOptions.lockSound());
        this.preventDropping = createOption(ListOptions.preventDropping());

        return new AbstractWidget[]{
                createOption(ListOptions.lockSlots()),
                this.showLock,

                this.hardLockSlots,
                this.lockSound,

                this.preventDropping,
                this.lockedSlotTextColorField
        };
    }

    @Override
    protected void activateButtons() {
        boolean lockSlotsEnabled = ModHelper.options().lockedSlots.enableLockedSlots;
        this.showLock.active = lockSlotsEnabled;
        this.hardLockSlots.active = lockSlotsEnabled;
        this.lockSound.active = lockSlotsEnabled;
        this.preventDropping.active = lockSlotsEnabled;
        this.lockedSlotTextColorField.active = lockSlotsEnabled;
    }

    @Override
    protected String youtubeLink() {
        return "https://www.youtube.com/watch?v=dwcWn7q6Q20&t=240s";
    }

    @Override
    protected Component getYouTubeVideoTooltip() {
        return Component.translatable("qualityofqueso.gui.showcase.locked_slots.tooltip");
    }

    @Override
    protected void blitYouTubeSprite(GuiGraphicsExtractor graphics) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ofQoQ(LOCKED_TEXTURE), this.youtubeButton.getX() + 12, this.youtubeButton.getY() - 6, 12, 12);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
        if (this.lockedSlotTextColorField.isHovered() && !this.lockedSlotTextColorField.isFocused()) {
            drawTooltip(this.lockedSlotTextColorField.getCurrentTooltip(), graphics, this.font, mouseX, mouseY);
        }
    }

    @Override
    public void onClose() {
        ModHelper.options().lockedSlots.lockedSlotColor = this.lockedSlotTextColorField.isSafeToSaveColor()
                ? ColorField.getTextColor(this.lockedSlotTextColorField.getValue())
                : this.lockedSlotTextColorField.getDefaultTextColor();
        super.onClose();
    }
}