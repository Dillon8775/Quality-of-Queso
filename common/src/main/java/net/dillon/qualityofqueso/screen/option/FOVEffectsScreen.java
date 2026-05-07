package net.dillon.qualityofqueso.screen.option;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class FOVEffectsScreen extends AbstractModOptionsScreen {

    public FOVEffectsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.fov_effects"));
    }

    @Override
    protected AbstractWidget[] options() {
        return new AbstractWidget[]{
                createOption(ListOptions.sprinting()),
                createOption(ListOptions.flying()),

                createOption(ListOptions.potionEffects()),
                createOption(ListOptions.fluids()),

                createOption(ListOptions.bows())
        };
    }
}