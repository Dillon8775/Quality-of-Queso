package net.dillon.qualityofqueso.screen;

import net.dillon.dillonlib.task.ClientTasks;
import net.dillon.qualityofqueso.config.ConfigurationScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static net.dillon.dillonlib.task.ClientTasks.*;
import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;
import static net.dillon.qualityofqueso.option.OptionInstances.*;

public class MainMenuScreen extends AbstractModScreen {
    private AbstractWidget openItemFrameSearchGUIOptions, visualTime, hudPositions, debugHuds;

    public MainMenuScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.options.title").withStyle(ChatFormatting.GOLD));
    }

    @Override
    protected AbstractWidget[] options() {
        this.openItemFrameSearchGUIOptions = Button.builder(Component.translatable("qualityofqueso.gui.open_item_frame_search_gui"), button -> {
            if (common().itemFrameSearching) {
                executeIfClientLevel(clientLevel -> {
                    openScreen(new ItemFrameSearchScreen(this));
                });
            }
        }).build();

        this.visualTime = Button.builder(Component.translatable("qualityofqueso.gui.visual_time"), button ->
                openScreen(new VisualTimeScreen(this))
        ).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.visual_time.description"))
        ).build();
        this.visualTime.active = mixins().clockManagerMixin;

        this.hudPositions = Button.builder(Component.translatable("qualityofqueso.gui.hud_positions"), button -> {
            openScreen(new HudPositionsScreen(this));
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.hud_positions.tooltip"))
        ).build();

        this.debugHuds = Button.builder(Component.translatable("qualityofqueso.gui.debug_huds"), button -> openDebugEntriesScreen("qualityofqueso")
        ).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.debug_huds.tooltip"))
        ).build();

        return new AbstractWidget[]{
                Button.builder(Component.translatable("qualityofqueso.gui.configure"), button -> ClientTasks.tryOpenYaclScreen(
                        () -> ConfigurationScreen.configScreen().generateScreen(this),
                        Component.translatable("qualityofqueso.title")
                )).tooltip(Tooltip.create(Component.translatable("qualityofqueso.gui.configure.tooltip"))).build(),

                this.openItemFrameSearchGUIOptions,

                Button.builder(Component.translatable("qualityofqueso.gui.keybinds"), button -> {
                    this.openKeybinds();
                }).tooltip(
                        Tooltip.create(Component.translatable("qualityofqueso.gui.keybinds.tooltip"))
                ).build(),

                this.visualTime,

                this.debugHuds,

                this.hudPositions,

                Button.builder(Component.translatable("qualityofqueso.gui.resources"), button -> {
                    openScreen(new ResourcesScreen(this));
                }).tooltip(
                        Tooltip.create(Component.translatable("qualityofqueso.gui.resources.tooltip"))
                ).build(),
        };
    }

    @Override
    protected void activateButtons() {
        boolean modEnabled = modEnabled(this.minecraft);
        this.openItemFrameSearchGUIOptions.active = modEnabled && common().itemFrameSearching && this.minecraft.level != null;
        this.visualTime.active = modEnabled;
        this.hudPositions.active = modEnabled && this.minecraft.level != null && (!client().hud().armorStatus.off() || client().itemCounter().itemCounter.enabled());
        this.debugHuds.active = modEnabled;
    }

    @Override
    public void onClose() {
        openScreen(this.lastScreen);
    }
}