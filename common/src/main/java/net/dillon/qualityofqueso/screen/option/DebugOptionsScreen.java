package net.dillon.qualityofqueso.screen.option;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class DebugOptionsScreen extends AbstractModOptionsScreen {

    public DebugOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.debug_huds"));
    }

    @Override
    protected AbstractWidget[] options() {
        return new AbstractWidget[]{
                createOption(ListOptions.inGameTime()),
                createOption(ListOptions.realLifeTime()),

                createOption(ListOptions.date()),
                createOption(ListOptions.simpleCoordinates()),

                createOption(ListOptions.accurateFacing()),
                createOption(ListOptions.fastestFlight())
        };
    }
}