package net.dillon.qualityofqueso.option.screen;

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
                ModListOptions.dragToSort(),
                ModListOptions.includeHotbar(),
                ModListOptions.showButtonShortcuts(),
                ModListOptions.legacyQuickMove()
        };
    }

    @Override
    protected void init() {
        super.init();
        this.body.addSingleOptionEntry(ModListOptions.transferring());
        this.body.addSingleOptionEntry(ModListOptions.containerSorting());
        this.body.addSingleOptionEntry(ModListOptions.quickDrop());
        this.body.addSingleOptionEntry(ModListOptions.swapping());
        this.body.addAll(this.options());
    }

    @Override
    protected boolean addOptionsByDefault() {
        return false;
    }
}