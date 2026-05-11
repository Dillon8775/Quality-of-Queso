package net.dillon.qualityofqueso.screen.option;

import net.dillon.qualityofqueso.helper.ModHelper;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ItemCounterOptionsScreen extends AbstractModOptionsScreen {
    private AbstractWidget displayOnPickup, arrowCounter, displayOnThrow, displayTotalWithStacks, onlyCountMatchingItems, countContainers, countEnderChest;

    public ItemCounterOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.item_counter_options"));
    }

    @Override
    protected AbstractWidget[] options() {
        this.displayOnPickup = createOption(ListOptions.displayOnPickup());
        this.arrowCounter = Button.builder(
                Component.translatable("qualityofqueso.gui.arrow_counter_options"),
                button -> this.minecraft.setScreen(new ArrowCounterOptionsScreen(this))
        ).tooltip(Tooltip.create(Component.translatable("qualityofqueso.gui.arrow_counter_options.tooltip"))).build();
        this.displayOnThrow = createOption(ListOptions.displayOnThrow());

        this.displayTotalWithStacks = createOption(ListOptions.displayTotalWithStacks());

        this.countContainers = createOption(ListOptions.countContainers());
        this.onlyCountMatchingItems = createOption(ListOptions.onlyCountMatchingItems());
        this.countEnderChest = createOption(ListOptions.countEnderChest());

        return new AbstractWidget[]{
                createOption(ListOptions.itemCounter()),
                this.arrowCounter,

                this.countContainers,
                this.displayOnPickup,

                this.countEnderChest,
                this.displayOnThrow,

                this.onlyCountMatchingItems,
                this.displayTotalWithStacks,
        };
    }

    @Override
    protected void activateButtons() {
        boolean itemCounterEnabled = ModHelper.options().itemCounter.enableItemCounter.enabled();
        this.displayOnPickup.active = itemCounterEnabled;
        this.arrowCounter.active = itemCounterEnabled;
        this.displayOnThrow.active = itemCounterEnabled;
        this.displayTotalWithStacks.active = itemCounterEnabled;
        this.countContainers.active = itemCounterEnabled;
        this.onlyCountMatchingItems.active = itemCounterEnabled;
        this.countEnderChest.active = itemCounterEnabled;
    }
}