package net.dillon.qualityofqueso.option.screen;

import net.dillon.qualityofqueso.option.ModListOptions;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ItemCounterOptionsScreen extends AbstractModOptionsScreen {

    public ItemCounterOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.item_counter_options"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
                ModListOptions.itemCounter(),
                ModListOptions.displayOnThrow(),

                ModListOptions.countContainers(),
                ModListOptions.displayOnPickup(),

                ModListOptions.showArrowCounter(),
                ModListOptions.displayTotalWithStacks(),

                ModListOptions.countAllArrows(),
                ModListOptions.onlyCountMatchingItems()
        };
    }
}