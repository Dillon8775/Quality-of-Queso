package net.dillon.qualityofqueso.option.screen;

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
                ModListOptions.chestSearching(),
                ModListOptions.searchInventory(),
                ModListOptions.inventorySearching(),
                ModListOptions.saveSearchText()
        };
    }
}