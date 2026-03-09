package net.dillon.qualityofqueso.screen.gui;

import net.dillon.qualityofqueso.util.ContainerTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static net.dillon.qualityofqueso.util.ModUtil.options;

/**
 * A button to only transfer what is present in the opposite container.
 */
public class FillWhatsPresentButton extends ToggleableButton {

    public FillWhatsPresentButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, int x, int y, String buttonName, OnPress onPress) {
        super(screenHandler, font, searchFieldText, x, y, buttonName, onPress,
                ContainerTracker.IS_TRACKED_CONTAINER
                        ? ContainerTracker.CURRENT_FILTER_MODE.tag() ? of("filtered_tag") : of("filtered")
                        : of("fill_whats_present"),
                ContainerTracker.IS_TRACKED_CONTAINER
                        ? ContainerTracker.CURRENT_FILTER_MODE.tag() ? of("filtered_tag_move_anything") : of("filtered_move_anything")
                        : of("move_anything"),
                new String[]{"qualityofqueso.gui.fill_whats_present/filtered", "qualityofqueso.gui.fill_whats_present/fill_whats_present", "qualityofqueso.gui.fill_whats_present/move_anything"}
        );
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        if (event.button() == 1 && ContainerTracker.IS_TRACKED_CONTAINER) {
            ContainerTracker.toggleCurrentFilterMode();
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }
        return super.mouseClicked(event, isDouble);
    }

    @Override
    boolean option() {
        return options().fillWhatsPreset;
    }

    /**
     * @return a string of "fill what's present".
     */
    private static String of(String s) {
        return "fill_whats_present/" + s;
    }
}