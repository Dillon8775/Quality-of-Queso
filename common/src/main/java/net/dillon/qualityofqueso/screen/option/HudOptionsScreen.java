package net.dillon.qualityofqueso.screen.option;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class HudOptionsScreen extends AbstractModOptionsScreen {

    public HudOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.hud_options"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
                ListOptions.armorStatus(),
                ListOptions.animations(),

                ListOptions.armorHotbar(),
                ListOptions.animationTime(),

                ListOptions.emptySlots(),
                ListOptions.displayTime()
        };
    }
}