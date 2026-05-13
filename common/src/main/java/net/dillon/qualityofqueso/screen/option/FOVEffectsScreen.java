package net.dillon.qualityofqueso.screen.option;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class FOVEffectsScreen extends AbstractModOptionsScreen {

    public FOVEffectsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.fov_effects"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
                ListOptions.sprinting(),
                ListOptions.flying(),

                ListOptions.potionEffects(),
                ListOptions.bows()
        };
    }
}