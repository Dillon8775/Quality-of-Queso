package net.dillon.qualityofqueso.screen.option;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class FogOptionsScreen extends AbstractModOptionsScreen {

    public FogOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.fog_options"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
                ListOptions.allFog(),
                ListOptions.overworldFog(),

                ListOptions.netherFog(),
                ListOptions.netherFogIntensity()
        };
    }
}