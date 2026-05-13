package net.dillon.qualityofqueso.screen.option;

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
                ListOptions.itemCounter(),
                ListOptions.arrowCounter(),

                ListOptions.onlyShowArrowCounter(),
                ListOptions.countAllArrows(),

                ListOptions.alwaysShowArrowCounter(),
                ListOptions.countContainers(),

                ListOptions.displayOnPickup(),
                ListOptions.countEnderChest(),

                ListOptions.displayOnThrow(),
                ListOptions.onlyCountMatchingItems(),

                ListOptions.displayTotalWithStacks(),
        };
    }
}