package net.dillon.qualityofqueso.option.options;

import net.dillon.qualityofqueso.option.AbstractModOptionsScreen;
import net.dillon.qualityofqueso.option.ModListOptions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class InventoryManagementOptionsScreen extends AbstractModOptionsScreen {

    public InventoryManagementOptionsScreen(Screen parent) {
        super(parent, Text.translatable("qualityofqueso.gui.title.inventory_management_options"));
    }

    @Override
    protected SimpleOption<?>[] options() {
        return new SimpleOption[]{
                ModListOptions.inventoryManagement(),
                ModListOptions.shortcutKeys(),
                ModListOptions.quickDrop(),
                ModListOptions.swapping(),
                ModListOptions.dragToSort(),
                ModListOptions.includeHotbar(),
                ModListOptions.showButtonOutlines(),
                ModListOptions.showButtonShortcuts(),
                ModListOptions.legacyQuickMove()
        };
    }
}