package net.dillon.qualityofqueso.screen.option;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class VisualTimeOptionsScreen extends AbstractModOptionsScreen {

    public VisualTimeOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.visual_time"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
                ListOptions.overrideClientTime(),
                ListOptions.visualTime(),

                ListOptions.visualTimeSpeed(),
                ListOptions.matchWithIRLTIme()
        };
    }
}