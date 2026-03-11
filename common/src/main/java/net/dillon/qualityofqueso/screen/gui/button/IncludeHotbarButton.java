package net.dillon.qualityofqueso.screen.gui.button;

import net.minecraft.client.gui.Font;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static net.dillon.qualityofqueso.util.ModUtil.options;

/**
 * A separate class for the include hotbar button.
 */
public class IncludeHotbarButton extends ToggleableButton {

    public IncludeHotbarButton(AbstractContainerMenu screenHandler, Font font, String searchFieldText, int x, int y, String buttonName, OnPress onPress) {
        super(screenHandler, font, searchFieldText, x, y, buttonName, onPress,
                "hotbar/include_hotbar_button",
                "hotbar/exclude_hotbar_button",
                new String[]{"qualityofqueso.gui.hotbar/include_hotbar", "qualityofqueso.gui.hotbar/exclude_hotbar"}
        );
    }

    @Override
    boolean option() {
        return options().management.includeHotbar;
    }
}