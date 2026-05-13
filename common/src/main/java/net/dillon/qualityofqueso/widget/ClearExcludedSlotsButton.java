package net.dillon.qualityofqueso.widget;

import net.dillon.qualityofqueso.option.ModClientOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static net.dillon.qualityofqueso.helper.ModHelper.options;

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

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int bl) {
        if (bl == 1) {
            ModClientOptions.INSTANCE.update((options) -> {
                options.management.saveExcludedSlots = !options.management.saveExcludedSlots;
            });
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, bl);
    }
}