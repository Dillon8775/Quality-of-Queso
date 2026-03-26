package net.dillon.qualityofqueso.option.screen;

import net.dillon.qualityofqueso.option.ModListOptions;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ButtonDisplayOptionsScreen extends AbstractModOptionsScreen {

    public ButtonDisplayOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.button_display_options"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
        };
    }

    @Override
    protected void init() {
        super.init();
        this.list.addBig(ModListOptions.displayIncludeHotbar());
        this.list.addBig(ModListOptions.displaySearchTransportables());
        this.list.addBig(ModListOptions.displayAlwaysQuickMove());
    }

    protected boolean addOptionsByDefault() {
        return false;
    }
}