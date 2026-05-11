package net.dillon.qualityofqueso.screen.option;

import net.dillon.qualityofqueso.helper.ModHelper;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ArrowCounterOptionsScreen extends AbstractModOptionsScreen {
    private AbstractWidget countAllArrows, onlyShowArrowCounter, alwaysShowArrowCounter;

    public ArrowCounterOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.arrow_counter_options"));
    }

    @Override
    protected AbstractWidget[] options() {
        this.countAllArrows = createOption(ListOptions.countAllArrows());
        this.onlyShowArrowCounter = createOption(ListOptions.onlyShowArrowCounter());
        this.alwaysShowArrowCounter = createOption(ListOptions.alwaysShowArrowCounter());

        return new AbstractWidget[]{
                createOption(ListOptions.arrowCounter()),
                this.onlyShowArrowCounter,

                this.countAllArrows,
                this.alwaysShowArrowCounter
        };
    }

    @Override
    protected void activateButtons() {
        boolean itemCounterEnabledAndArrowCounter = ModHelper.options().itemCounter.enableItemCounter.enabled() && ModHelper.options().itemCounter.arrowCounter;
        this.countAllArrows.active = itemCounterEnabledAndArrowCounter;
        this.onlyShowArrowCounter.active = itemCounterEnabledAndArrowCounter;
        this.alwaysShowArrowCounter.active = itemCounterEnabledAndArrowCounter;
    }
}