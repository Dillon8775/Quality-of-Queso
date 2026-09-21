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

    public MainMenuScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.title").withStyle(ChatFormatting.GOLD));
    }

    @Override
    public void widgets() {
        boolean modEnabled = modEnabled(this.minecraft);

        AbstractWidget configure = this.createWidget(
                Button.builder(Component.translatable("qualityofqueso.menu.configure"), button -> ClientTasks.tryOpenYaclScreen(
                        () -> ConfigurationScreen.configScreen().generateScreen(this),
                        Component.translatable("qualityofqueso")
                )).build()
        );

        AbstractWidget keybinds = this.createWidget(
                this.createOpenKeybindsButton(Component.translatable("qualityofqueso.menu.keybinds"))
        );

        AbstractWidget openItemFrameSearchGUIOptions = this.createWidget(
                Button.builder(Component.translatable("qualityofqueso.menu.open_item_frame_search_gui"), button -> {
                    if (common().itemFrameSearching) {
                        executeIfClientLevel(clientLevel -> {
                            openScreen(new ItemFrameSearchScreen(this));
                        });
                    }
                }).build(),
                modEnabled && common().itemFrameSearching && this.minecraft.level != null
        );

        AbstractWidget visualTime = this.createWidget(
                Button.builder(Component.translatable("qualityofqueso.menu.visual_time"), button ->
                        openScreen(new VisualTimeScreen(this))
                ).tooltip(
                        Tooltip.create(Component.translatable("qualityofqueso.menu.visual_time.description"))
                ).build(),
                modEnabled && mixins().clockManagerMixin
        );

        AbstractWidget hudPositions = this.createWidget(
                Button.builder(Component.translatable("qualityofqueso.menu.hud_positions"), button -> {
                    openScreen(new HudPositionsScreen(this));
                }).tooltip(
                        Tooltip.create(Component.translatable("qualityofqueso.menu.hud_positions.tooltip"))
                ).build(),
                modEnabled && this.minecraft.level != null && (!client().hud().armorStatus.off() || client().itemCounter().itemCounter.enabled())
        );

        AbstractWidget debugHuds = this.createWidget(
                Button.builder(Component.translatable("qualityofqueso.menu.debug_huds"), button -> openDebugEntriesScreen(this, "qualityofqueso")
                ).tooltip(
                        Tooltip.create(Component.translatable("qualityofqueso.menu.debug_huds.tooltip"))
                ).build(),
                modEnabled
        );

        AbstractWidget resources = this.createWidget(
                Button.builder(Component.translatable("qualityofqueso.menu.resources"), button -> {
                    openScreen(new ResourcesScreen(this));
                }).tooltip(
                        Tooltip.create(Component.translatable("qualityofqueso.menu.resources.tooltip"))
                ).build()
        );

        this.createHeaderWithBig(
                Component.translatable("qualityofqueso.header.settings"),
                configure,
                keybinds,
                hudPositions
        );

        this.createHeader(
                Component.translatable("qualityofqueso.header.utilities"),
                visualTime,
                openItemFrameSearchGUIOptions,
                debugHuds,
                resources
        );
    }

    @Override
    public void onClose() {
        openScreen(this.lastScreen);
    }
}