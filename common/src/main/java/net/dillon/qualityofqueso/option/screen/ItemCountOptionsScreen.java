package net.dillon.qualityofqueso.option.screen;

import net.dillon.qualityofqueso.option.ModListOptions;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ItemCountOptionsScreen extends AbstractModOptionsScreen {

    public ItemCountOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.item_count_options"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
                ModListOptions.itemCount(),
                ModListOptions.countContainers(),

                ModListOptions.displayOnThrow(),
                ModListOptions.displayOnPickup(),

                ModListOptions.showArrowCount(),
                ModListOptions.countAllArrows()
        };
    }
}