package net.dillon.qualityofqueso.option.screen;

import net.dillon.qualityofqueso.option.ModListOptions;
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
                ModListOptions.sprinting(),
                ModListOptions.flying(),

                ModListOptions.potionEffects(),
                ModListOptions.bows()
        };
    }
}