package net.dillon.qualityofqueso.screen.option;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Deprecated
public class LockedSlotsOptionsScreen extends AbstractModOptionsScreen {

    public LockedSlotsOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.locked_slots"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
        };
    }

    @Override
    protected String youtubeLink() {
        return "https://www.youtube.com/watch?v=dwcWn7q6Q20&t=240s";
    }

    @Override
    protected Component getYouTubeVideoTooltip() {
        return Component.translatable("qualityofqueso.gui.showcase.locked_slots.tooltip");
    }
}