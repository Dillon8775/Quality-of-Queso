package net.dillon.qualityofqueso.screen.option;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ManagementOptionsScreen extends AbstractModOptionsScreen {

    public ManagementOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.management_options"));
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
                ListOptions.transferring(),
                ListOptions.sorting(),

                ListOptions.useGlobalSortingMode(),
                ListOptions.globalSortingMode(),

                ListOptions.filtering(),
                ListOptions.quickDrop(),

                ListOptions.singularMoving(),
                ListOptions.dragMoving(),

                ListOptions.swapping(),
                ListOptions.dragSorting(),

                ListOptions.layout(),
                ListOptions.lockSlots(),

                ListOptions.showLock(),
                ListOptions.hardLockSlots(),

                ListOptions.lockSound(),
                ListOptions.playSounds(),
        };
    }

    @Override
    protected String youtubeLink() {
        return "https://youtu.be/02wfcgHkPmQ?si=YBNKVa_53pOtWa84&t=231";
    }

    @Override
    protected Component getYouTubeVideoTooltip() {
        return Component.translatable("qualityofqueso.gui.showcase.management.tooltip");
    }
}