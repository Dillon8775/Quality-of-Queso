package net.dillon.qualityofqueso.screen;

import net.dillon.dillonlib.task.ClientTasks;
import net.dillon.qualityofqueso.config.ConfigurationScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

import static net.dillon.dillonlib.task.ClientTasks.*;
import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;
import static net.dillon.qualityofqueso.option.OptionInstances.*;

public class MainMenuScreen extends AbstractModScreen {
    private AbstractWidget configure, openItemFrameSearchGUIOptions, keybinds, visualTime, hudPositions, debugHuds, resources;

    public MainMenuScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.menu.title").withStyle(ChatFormatting.GOLD));
    }

    @Override
    protected void init() {
        super.init();

        this.configure = Button.builder(Component.translatable("qualityofqueso.menu.configure"), button -> ClientTasks.tryOpenYaclScreen(
                () -> ConfigurationScreen.configScreen().generateScreen(this),
                Component.translatable("qualityofqueso.title")
        )).build();

        this.keybinds = Button.builder(Component.translatable("qualityofqueso.menu.keybinds"), button -> {
            this.openKeybinds();
        }).build();

        this.openItemFrameSearchGUIOptions = Button.builder(Component.translatable("qualityofqueso.menu.open_item_frame_search_gui"), button -> {
            if (common().itemFrameSearching) {
                executeIfClientLevel(clientLevel -> {
                    openScreen(new ItemFrameSearchScreen(this));
                });
            }
        }).build();

        this.visualTime = Button.builder(Component.translatable("qualityofqueso.menu.visual_time"), button ->
                openScreen(new VisualTimeScreen(this))
        ).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.menu.visual_time.description"))
        ).build();
        this.visualTime.active = mixins().clockManagerMixin;

        this.hudPositions = Button.builder(Component.translatable("qualityofqueso.menu.hud_positions"), button -> {
            openScreen(new HudPositionsScreen(this));
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.menu.hud_positions.tooltip"))
        ).build();

        this.debugHuds = Button.builder(Component.translatable("qualityofqueso.menu.debug_huds"), button -> openDebugEntriesScreen(this, "qualityofqueso")
        ).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.menu.debug_huds.tooltip"))
        ).build();

        this.resources = Button.builder(Component.translatable("qualityofqueso.menu.resources"), button -> {
            openScreen(new ResourcesScreen(this));
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.menu.resources.tooltip"))
        ).build();

        this.list.addHeader(Component.translatable("qualityofqueso.menu.settings"));
        this.list.addBig(this.configure);
        this.list.addSmall(
                List.of(
                        this.keybinds,
                        this.hudPositions
                )
        );

        this.list.addHeader(Component.translatable("qualityofqueso.menu.utilities"));
        this.list.addSmall(
                List.of(
                        this.visualTime,
                        this.openItemFrameSearchGUIOptions,
                        this.debugHuds,
                        this.resources
                )
        );
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