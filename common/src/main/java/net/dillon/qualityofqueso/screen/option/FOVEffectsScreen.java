package net.dillon.qualityofqueso.screen.option;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class FOVEffectsScreen extends AbstractModOptionsScreen {
    private AbstractWidget fluids;

    public FOVEffectsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.fov_effects"));
    }

    @Override
    protected AbstractWidget[] options() {
        this.fluids = createOption(ListOptions.fluids());

        return new AbstractWidget[]{
                createOption(ListOptions.sprinting()),
                createOption(ListOptions.flying()),

                createOption(ListOptions.potionEffects()),
                this.fluids,

                createOption(ListOptions.bows())
        };
    }

    /**
     * Exclusive to <= 1.21.11.
     */
    @Override
    protected void activateButtons() {
        this.fluids.active = false;
    }
}