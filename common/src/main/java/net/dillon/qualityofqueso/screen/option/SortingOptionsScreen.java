package net.dillon.qualityofqueso.screen.option;

import net.dillon.qualityofqueso.helper.ModHelper;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SortingOptionsScreen extends AbstractModOptionsScreen {
    private AbstractWidget defaultSortingMode, useGlobalSortingMode, globalSortingMode;

    public SortingOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.sorting"));
    }

    @Override
    protected AbstractWidget[] options() {
        this.defaultSortingMode = createOption(ListOptions.defaultSortingMode());
        this.useGlobalSortingMode = createOption(ListOptions.useGlobalSortingMode());
        this.globalSortingMode = createOption(ListOptions.globalSortingMode());

        return new AbstractWidget[]{
                createOption(ListOptions.sorting()),
                this.defaultSortingMode,

                this.useGlobalSortingMode,
                this.globalSortingMode
        };
    }

    @Override
    protected void activateButtons() {
        boolean sortingActive = ModHelper.options().sorting.sortingEnabled.buttonOrKeyOrKeyOnly();
        boolean useGlobalSortingMode = ModHelper.options().sorting.useGlobalSortingMode;
        this.defaultSortingMode.active = sortingActive && !useGlobalSortingMode;
        this.useGlobalSortingMode.active = sortingActive;
        this.globalSortingMode.active = sortingActive && useGlobalSortingMode;
    }
}