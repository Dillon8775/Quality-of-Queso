package net.dillon.qualityofqueso.widget;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * A button to clear all excluded slots on the screen.
 */
public class ClearExcludedSlotsButton extends ToggleableButton {

    public ClearExcludedSlotsButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, String buttonName, OnPress onPress) {
        super(screenHandler, font, searchFieldText, buttonName, onPress);
    }

    @Override
    protected String onTextureId() {
        return "clear_excluded_slots/clear_excluded_slots_saving";
    }

    @Override
    protected String offTextureId() {
        return "clear_excluded_slots/clear_excluded_slots";
    }

    @Override
    protected boolean option() {
        return ModConstants.SAVING_EXCLUDED_SLOTS;
    }

    @Override
    protected Component getTooltipToRender() {
        return Component.translatable("qualityofqueso.gui.clear_excluded_slots",
                Component.translatable(this.option()
                        ? "qualityofqueso.gui.saving_slots"
                        : "qualityofqueso.gui.save_slots"
                )
        );
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT) {
            ModConstants.SAVING_EXCLUDED_SLOTS = !ModConstants.SAVING_EXCLUDED_SLOTS;
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }
        return super.mouseClicked(event, isDouble);
    }
}