package net.dillon.qualityofqueso.option.screen;

import net.dillon.qualityofqueso.option.ModListOptions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class ItemFrameSearchingOptionsScreen extends AbstractModOptionsScreen {

    public ItemFrameSearchingOptionsScreen(Screen parent) {
        super(parent, Text.translatable("qualityofqueso.gui.title.item_frame_searching_options"));
    }

    @Override
    protected SimpleOption<?>[] options() {
        return new SimpleOption[]{
                ModListOptions.itemFrameSearching(),
                ModListOptions.itemFrameSearchGlowDuration(),
                ModListOptions.itemFrameSearchRadius(),
                ModListOptions.saveSearchText()
        };
    }
}