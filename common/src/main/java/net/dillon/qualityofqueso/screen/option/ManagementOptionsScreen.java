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

                ListOptions.defaultSortingMode(),
                ListOptions.dragMoving(),

                ListOptions.scrollMoving(),
                ListOptions.dragSorting(),

                ListOptions.containerFiltering(),
                ListOptions.quickDrop(),

                ListOptions.swapping(),
                ListOptions.lockSlots(),

                ListOptions.layout(),
                ListOptions.showLock(),

                ListOptions.hardLockSlots(),
                ListOptions.lockSound(),

                ListOptions.preventDropping(),
                ListOptions.playSounds(),

                ListOptions.inventoryLocking()
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