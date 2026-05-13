package net.dillon.qualityofqueso.screen.option;

import net.dillon.qualityofqueso.helper.ModHelper;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class FogOptionsScreen extends AbstractModOptionsScreen {
    private AbstractWidget overworldFog, overworldFogIntensity, netherFog, netherFogIntensity;

    public FogOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.fog_options"));
    }

    @Override
    protected AbstractWidget[] options() {
        this.overworldFog = createOption(ListOptions.overworldFog());
        this.overworldFogIntensity = createOption(ListOptions.overworldFogIntensity());

        this.netherFog = createOption(ListOptions.netherFog());
        this.netherFogIntensity = createOption(ListOptions.netherFogIntensity());

        return new AbstractWidget[]{
                createOption(ListOptions.allFog()),
                this.overworldFog,

                this.netherFog,
                this.overworldFogIntensity,

                this.netherFogIntensity
        };
    }

    @Override
    protected void activateButtons() {
        boolean fogActive = ModHelper.options().fog.allFog;
        this.overworldFog.active = fogActive;
        this.overworldFogIntensity.active = false;
        this.netherFog.active = fogActive;
        this.netherFogIntensity.active = fogActive && ModHelper.options().fog.netherFog;
    }
}