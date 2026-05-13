package net.dillon.qualityofqueso.screen.option;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Deprecated
public class ArrowCounterOptionsScreen extends AbstractModOptionsScreen {

    public ArrowCounterOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.arrow_counter_options"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
        };
    }
}