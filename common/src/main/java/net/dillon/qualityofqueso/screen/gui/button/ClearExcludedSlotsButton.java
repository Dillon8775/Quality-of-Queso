package net.dillon.qualityofqueso.screen.gui.button;

import net.dillon.qualityofqueso.option.instance.ModClientOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static net.dillon.qualityofqueso.util.ModUtil.options;

/**
 * A button to clear all excluded slots on the screen.
 */
public class ClearExcludedSlotsButton extends ToggleableButton {

    public ClearExcludedSlotsButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, int x, int y, String buttonName, OnPress onPress) {
        super(screenHandler, font, searchFieldText, x, y, buttonName, onPress);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        if (event.button() == 1) {
            options().management.saveExcludedSlots = !options().management.saveExcludedSlots;
            ModClientOptions.CLIENT.save();
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }
        return super.mouseClicked(event, isDouble);
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
        return options().management.saveExcludedSlots;
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
}