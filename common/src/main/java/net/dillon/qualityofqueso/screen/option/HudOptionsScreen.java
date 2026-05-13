package net.dillon.qualityofqueso.screen.option;

import net.dillon.qualityofqueso.helper.ModHelper;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class HudOptionsScreen extends AbstractModOptionsScreen {
    private AbstractWidget armorHotbar, emptySlots, highlightArmor, animationTime;

    public HudOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.hud_options"));
    }

    @Override
    protected AbstractWidget[] options() {
        this.armorHotbar = createOption(ListOptions.armorHotbar());
        this.emptySlots = createOption(ListOptions.emptySlots());
        this.highlightArmor = createOption(ListOptions.highlightArmor());
        this.animationTime = createOption(ListOptions.animationTime());

        return new AbstractWidget[]{
                createOption(ListOptions.armorStatus()),
                createOption(ListOptions.animations()),

                this.armorHotbar,
                this.animationTime,

                this.emptySlots,
                createOption(ListOptions.displayTime()),

                this.highlightArmor,
                createOption(ListOptions.warningIndicators()),

                createOption(ListOptions.coloredHighlighting()),
        };
    }

    @Override
    protected void activateButtons() {
        boolean armorStatus = !ModHelper.options().hud.armorStatus.off();
        this.armorHotbar.active = armorStatus;
        this.emptySlots.active = armorStatus;
        this.highlightArmor.active = armorStatus;
        this.animationTime.active = ModHelper.options().hud.animations;
    }
}