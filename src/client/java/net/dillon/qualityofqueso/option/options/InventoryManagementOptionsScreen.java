package net.dillon.qualityofqueso.option.options;

import net.dillon.qualityofqueso.option.AbstractModOptionsScreen;
import net.dillon.qualityofqueso.option.ModListOptions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class InventoryManagementOptionsScreen extends AbstractModOptionsScreen {

    public InventoryManagementOptionsScreen(Screen parent) {
        super(parent, Text.translatable("qualityofqueso.gui.inventory_management_options"));
    }

    @Override
    protected SimpleOption<?>[] options() {
        return new SimpleOption[]{
                ModListOptions.INVENTORY_MANAGEMENT,
                ModListOptions.REQUIRE_ALT_TO_MOVE,
                ModListOptions.INCLUDE_HOTBAR
        };
    }
}