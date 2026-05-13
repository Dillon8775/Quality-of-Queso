package net.dillon.qualityofqueso.screen.option;

import net.dillon.qualityofqueso.helper.ModHelper;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class VisualTimeOptionsScreen extends AbstractModOptionsScreen {
    private AbstractWidget visualTime, visualTimeSpeed, matchWithIRLTime;

    public VisualTimeOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.visual_time"));
    }

    @Override
    protected AbstractWidget[] options() {
        return new AbstractWidget[]{
                createOption(ListOptions.overrideClientTime()),
                this.visualTime = createOption(ListOptions.visualTime()),

                this.visualTimeSpeed = createOption(ListOptions.visualTimeSpeed()),
                this.matchWithIRLTime = createOption(ListOptions.matchWithIRLTIme())
        };
    }

    @Override
    protected void activateButtons() {
        boolean overrideClientTime = ModHelper.options().visualTime.overrideClientTime;
        boolean matchWithIRLTime = ModHelper.options().visualTime.matchWithIrlTime;
        this.visualTime.active = overrideClientTime && ModHelper.options().visualTime.visualTimeSpeed == 0 && !matchWithIRLTime;
        this.visualTimeSpeed.active = overrideClientTime && !matchWithIRLTime;
        this.matchWithIRLTime.active = overrideClientTime;
    }
}