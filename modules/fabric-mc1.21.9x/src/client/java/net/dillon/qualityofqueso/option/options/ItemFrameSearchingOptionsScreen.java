package net.dillon.qualityofqueso.option.options;

import net.dillon.qualityofqueso.option.AbstractModOptionsScreen;
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
                ModListOptions.ITEM_FRAME_SEARCH_TIMER,
                ModListOptions.ITEM_FRAME_SEARCH_RADIUS,
                ModListOptions.SAVE_SEARCH_TEXT
        };
    }
}