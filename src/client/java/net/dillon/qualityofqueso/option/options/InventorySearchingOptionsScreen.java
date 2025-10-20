package net.dillon.qualityofqueso.option.options;

import net.dillon.qualityofqueso.option.AbstractModOptionsScreen;
import net.dillon.qualityofqueso.option.ModListOptions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class InventorySearchingOptionsScreen extends AbstractModOptionsScreen {

    public InventorySearchingOptionsScreen(Screen parent) {
        super(parent, Text.translatable("qualityofqueso.gui.title.inventory_searching_options"));
    }

    @Override
    protected SimpleOption<?>[] options() {
        return new SimpleOption[]{
                ModListOptions.CHEST_SEARCHING,
                ModListOptions.SEARCH_INVENTORY,
                ModListOptions.INVENTORY_SEARCHING,
                ModListOptions.SAVE_SEARCH_TEXT
        };
    }
}