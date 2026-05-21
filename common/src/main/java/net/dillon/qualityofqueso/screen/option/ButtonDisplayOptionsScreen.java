package net.dillon.qualityofqueso.screen.option;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ButtonDisplayOptionsScreen extends AbstractModOptionsScreen {

    public ButtonDisplayOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.button_display_options"));
    }

    @Override
    protected AbstractWidget[] options() {
        return new AbstractWidget[]{
                createOption(ListOptions.displayIncludeHotbar()),
                createOption(ListOptions.displayFiltering()),

                createOption(ListOptions.displaySearchTransportables()),
                createOption(ListOptions.displayAlwaysQuickMove()),

                createOption(ListOptions.displayBulkCraft()),
                createOption(ListOptions.displayBulkTrade()),

                createOption(ListOptions.safeBulk()),
                createOption(ListOptions.displayLockInventory())
        };
    }

    @Override
    protected String youtubeLink() {
        return "https://www.youtube.com/watch?v=dwcWn7q6Q20&t=177s";
    }

    @Override
    protected Component getYouTubeVideoTooltip() {
        return Component.translatable("qualityofqueso.gui.showcase.management.tooltip");
    }
}