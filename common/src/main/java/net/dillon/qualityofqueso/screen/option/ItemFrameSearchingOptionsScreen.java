package net.dillon.qualityofqueso.screen.option;

import net.dillon.qualityofqueso.helper.ModHelper;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ItemFrameSearchingOptionsScreen extends AbstractModOptionsScreen {
    private AbstractWidget itemFrameSearchGlowDuration, itemFrameSearchRadius;

    public ItemFrameSearchingOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.title.item_frame_searching_options"));
    }

    @Override
    protected AbstractWidget[] options() {
        this.itemFrameSearchGlowDuration = createOption(ListOptions.itemFrameSearchGlowDuration());
        this.itemFrameSearchRadius = createOption(ListOptions.itemFrameSearchRadius());

        return new AbstractWidget[]{
                createOption(ListOptions.itemFrameSearching()),
                this.itemFrameSearchGlowDuration,

                this.itemFrameSearchRadius,
                createOption(ListOptions.saveSearchText())
        };
    }

    @Override
    protected void activateButtons() {
        boolean itemFrameSearchingEnabled = ModHelper.coptions().itemFrameSearching;
        this.itemFrameSearchGlowDuration.active = itemFrameSearchingEnabled;
        this.itemFrameSearchRadius.active = itemFrameSearchingEnabled;
    }
}