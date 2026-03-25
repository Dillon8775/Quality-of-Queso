package net.dillon.qualityofqueso.option.screen;

import net.dillon.qualityofqueso.option.ModListOptions;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ManagementOptionsScreen extends AbstractModOptionsScreen {

    public ManagementOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.management_options"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
                ModListOptions.dragSorting(),
                ModListOptions.tagSorting(),
                ModListOptions.horizontalLayout()
        };
    }

    @Override
    protected void init() {
        super.init();
        this.list.addBig(ModListOptions.transferring());
        this.list.addBig(ModListOptions.containerSorting());
        this.list.addBig(ModListOptions.containerFiltering());
        this.list.addBig(ModListOptions.quickDrop());
        this.list.addBig(ModListOptions.swapping());
        this.list.addSmall(this.options());
    }

    @Override
    protected boolean addOptionsByDefault() {
        return false;
    }
}