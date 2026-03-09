package net.dillon.qualityofqueso.option.screen;

import net.dillon.qualityofqueso.option.ModListOptions;
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
                ModListOptions.chestSearching(),
                ModListOptions.searchInventory(),
                ModListOptions.inventorySearching(),
                ModListOptions.saveSearchText()
        };
    }
}