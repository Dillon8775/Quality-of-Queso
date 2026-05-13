package net.dillon.qualityofqueso.screen.option;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Deprecated
public class SortingOptionsScreen extends AbstractModOptionsScreen {

    public SortingOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.sorting"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
        };
    }
}