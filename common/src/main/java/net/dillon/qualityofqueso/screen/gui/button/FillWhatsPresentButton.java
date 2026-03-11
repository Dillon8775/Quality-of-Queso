package net.dillon.qualityofqueso.screen.gui.button;

import net.dillon.qualityofqueso.screen.FilterItemsScreen;
import net.dillon.qualityofqueso.util.ContainerTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static net.dillon.qualityofqueso.util.ModUtil.options;

/**
 * A button to only transfer what is present in the opposite container.
 */
public class FillWhatsPresentButton extends ToggleableButton {
    private final Minecraft minecraft;
    private final AbstractContainerScreen<?> parent;

    public FillWhatsPresentButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, int x, int y, String buttonName, OnPress onPress, Minecraft minecraft, AbstractContainerScreen<?> parent) {
        super(screenHandler, font, searchFieldText, x, y, buttonName, onPress,
                ContainerTracker.IS_TRACKED_CONTAINER
                        ? ContainerTracker.CURRENT_FILTER_MODE.tag() ? of("filtered_tag") : of("filtered")
                        : of("fill_whats_present"),
                of("move_anything"),
                new String[]{"qualityofqueso.gui.fill_whats_present/filtered", "qualityofqueso.gui.fill_whats_present/fill_whats_present", "qualityofqueso.gui.fill_whats_present/move_anything"}
        );
        this.minecraft = minecraft;
        this.parent = parent;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDouble) {
        if (ContainerTracker.IS_TRACKED_CONTAINER) {
            if (event.button() == 1) {
                ContainerTracker.OPENING_PLACEHOLDER_SCREEN = true;
                this.minecraft.setScreen(new FilterItemsScreen(this.parent));
            } else {
                ContainerTracker.toggleCurrentFilterMode();
                this.playDownSound(Minecraft.getInstance().getSoundManager());
            }
            return true;
        }
        return super.mouseClicked(event, isDouble);
    }

    @Override
    boolean option() {
        return options().management.fillWhatsPreset;
    }

    /**
     * @return a string of "fill what's present".
     */
    private static String of(String s) {
        return "fill_whats_present/" + s;
    }
}