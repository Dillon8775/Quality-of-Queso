package net.dillon.qualityofqueso.screen.option;

import net.dillon.qualityofqueso.helper.ModHelper;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SortingOptionsScreen extends AbstractModOptionsScreen {
    private AbstractWidget useGlobalSortingMode, globalSortingMode;

    public SortingOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.sorting"));
    }

    @Override
    protected AbstractWidget[] options() {
        this.useGlobalSortingMode = createOption(ListOptions.useGlobalSortingMode());
        this.globalSortingMode = createOption(ListOptions.globalSortingMode());

        return new AbstractWidget[]{
                createOption(ListOptions.sorting()),

                this.useGlobalSortingMode,
                this.globalSortingMode
        };
    }

    @Override
    protected void activateButtons() {
        boolean sortingActive = ModHelper.options().sorting.sortingEnabled.buttonOrKeyOrKeyOnly();
        this.useGlobalSortingMode.active = sortingActive;
        this.globalSortingMode.active = sortingActive && ModHelper.options().sorting.useGlobalSortingMode;
    }
}