package net.dillon.qualityofqueso.option.screen;

import net.dillon.qualityofqueso.option.ModListOptions;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ItemFrameSearchingOptionsScreen extends AbstractModOptionsScreen {

    public ItemFrameSearchingOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.item_frame_searching_options"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
                ModListOptions.itemFrameSearching(),
                ModListOptions.itemFrameSearchGlowDuration(),
                ModListOptions.itemFrameSearchRadius(),
                ModListOptions.saveSearchText()
        };
    }
}