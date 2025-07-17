package net.dillon.qualityofqueso.option.options;

import net.dillon.qualityofqueso.option.AbstractModOptionsScreen;
import net.dillon.qualityofqueso.option.ModListOptions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class ChestSearchingOptionsScreen extends AbstractModOptionsScreen {

    public ChestSearchingOptionsScreen(Screen parent) {
        super(parent, Text.translatable("qualityofqueso.gui.chest_searching_options"));
    }

    @Override
    protected SimpleOption<?>[] options() {
        return new SimpleOption[]{
                ModListOptions.CHEST_SEARCH,
                ModListOptions.SEARCH_INVENTORY,
                ModListOptions.SAVE_SEARCH_TEXT
        };
    }
}