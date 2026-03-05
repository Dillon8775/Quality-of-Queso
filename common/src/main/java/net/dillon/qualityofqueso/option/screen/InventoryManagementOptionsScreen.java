package net.dillon.qualityofqueso.option.screen;

import net.dillon.qualityofqueso.option.ModListOptions;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class InventoryManagementOptionsScreen extends AbstractModOptionsScreen {

    public InventoryManagementOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.inventory_management_options"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
                ModListOptions.dragSorting(),
                ModListOptions.saveExcludedSlots(),

                ModListOptions.showButtonShortcuts(),
                ModListOptions.includeHotbar()
        };
    }

    @Override
    protected void init() {
        super.init();
        this.list.addBig(ModListOptions.transferring());
        this.list.addBig(ModListOptions.containerSorting());
        this.list.addBig(ModListOptions.quickDrop());
        this.list.addBig(ModListOptions.swapping());
        this.list.addBig(ModListOptions.perpendicularQuickMoving());
        this.list.addBig(ModListOptions.moveItemsIf());
        this.list.addSmall(this.options());
    }

    @Override
    protected boolean addOptionsByDefault() {
        return false;
    }
}