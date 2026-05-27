package net.dillon.qualityofqueso.screen;

import net.dillon.qualityofqueso.util.ListOptions;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class DebugOptionsScreen extends AbstractModScreen {

    public DebugOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.options.debug_huds"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
                ListOptions.inGameTime(),
                ListOptions.realLifeTime(),

                ListOptions.date(),
                ListOptions.simpleCoordinates(),

                ListOptions.accurateFacing(),
                ListOptions.fastestFlight()
        };
    }
}