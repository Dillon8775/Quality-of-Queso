package net.dillon.qualityofqueso.widget.management.toggleable;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.widget.management.ManagementParams;
import net.dillon.qualityofqueso.widget.management.ManagementSprites;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import static net.dillon.dillonlib.task.ClientTasks.getSoundManager;
import static net.dillon.qualityofqueso.widget.management.ManagementSprites.managementIdentifier;

/**
 * A management button that clears all {@code excluded slots} on a screen.
 */
public class ClearExcludedSlotsButton extends ToggleableButton {

    public ClearExcludedSlotsButton(ManagementParams params, OnPress onPress) {
        super(params, onPress);
    }

    @Override
    protected boolean toggle() {
        return ModConstants.SAVING_EXCLUDED_SLOTS;
    }

    @Override
    protected ManagementSprites getManagementSprites() {
        return new ManagementSprites(
                managementIdentifier("clear_excluded_slots/clear_excluded_slots_saving"),
                managementIdentifier("clear_excluded_slots/clear_excluded_slots")
        );
    }

    @Override
    protected Component getDefaultTooltip() {
        return Component.translatable("qualityofqueso.gui.clear_excluded_slots",
                Component.translatable(this.toggle()
                        ? "qualityofqueso.gui.saving_slots"
                        : "qualityofqueso.gui.save_slots"
                )
        );
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT) {
            ModConstants.SAVING_EXCLUDED_SLOTS = !ModConstants.SAVING_EXCLUDED_SLOTS;
            this.playDownSound(getSoundManager());
            return true;
        }
        return super.mouseClicked(event, isDouble);
    }
}