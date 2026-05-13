package net.dillon.qualityofqueso.screen.option;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SearchingOptionsScreen extends AbstractModOptionsScreen {

    public SearchingOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.searching_options"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
                ListOptions.containerSearching(),
                ListOptions.inventorySearching(),

                ListOptions.quickSearch(),
                ListOptions.searchBarPosition(),

                ListOptions.saveSearchText(),
        };
    }

    @Override
    protected String youtubeLink() {
        return "https://youtu.be/02wfcgHkPmQ?si=uBMyxYaKbsZn6WMC&t=90";
    }

    @Override
    protected Component getYouTubeVideoTooltip() {
        return Component.translatable("qualityofqueso.gui.showcase.searching.tooltip");
    }
}