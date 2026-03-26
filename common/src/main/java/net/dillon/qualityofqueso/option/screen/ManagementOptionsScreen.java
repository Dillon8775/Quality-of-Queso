package net.dillon.qualityofqueso.option.screen;

import net.dillon.qualityofqueso.option.ModListOptions;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ManagementOptionsScreen extends AbstractModOptionsScreen {

    public ManagementOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.management_options"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
                ModListOptions.dragSorting(),
                ModListOptions.tagSorting()
        };
    }

    @Override
    protected void init() {
        super.init();
        this.list.addBig(ModListOptions.buttonLayout());
        this.list.addBig(ModListOptions.transferring());
        this.list.addBig(ModListOptions.containerSorting());
        this.list.addBig(ModListOptions.containerFiltering());
        this.list.addBig(ModListOptions.quickDrop());
        this.list.addBig(ModListOptions.swapping());
        this.list.addSmall(this.options());

        List<AbstractWidget> options = new ArrayList<>(List.of(
                ModListOptions.buttonSounds().createButton(this.options),
                Button.builder(Component.translatable("qualityofqueso.gui.button_display_options"), button -> {
                    this.minecraft.setScreen(new ButtonDisplayOptionsScreen(this));
                }).tooltip(Tooltip.create(Component.translatable("qualityofqueso.gui.button_display_options.tooltip"))).build()
        ));
        this.list.addSmall(options);
    }

    @Override
    protected boolean addOptionsByDefault() {
        return false;
    }
}