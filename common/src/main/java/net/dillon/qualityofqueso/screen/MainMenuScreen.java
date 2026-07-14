package net.dillon.qualityofqueso.screen;

import net.dillon.qualityofqueso.config.ConfigurationScreen;
import net.dillon.qualityofqueso.platform.MultiLoader;
import net.dillon.qualityofqueso.platform.PlatformMod;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.helper.ModHelper.*;

public class MainMenuScreen extends AbstractModScreen {
    private AbstractWidget openItemFrameSearchGUIOptions, visualTime, hudPositions, debugHuds;

    public MainMenuScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.options.title").withStyle(ChatFormatting.GOLD));
    }

    @Override
    protected AbstractWidget[] options() {
        this.openItemFrameSearchGUIOptions = Button.builder(Component.translatable("qualityofqueso.gui.open_item_frame_search_gui"), button -> {
            if (commonOptionsInstance().itemFrameSearching && this.minecraft.level != null) {
                this.minecraft.setScreen(new ItemFrameSearchScreen(this));
            }
        }).build();

        this.visualTime = Button.builder(Component.translatable("qualityofqueso.gui.visual_time"), button ->
                this.minecraft.setScreen(new VisualTimeScreen(this))
        ).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.visual_time.description"))
        ).build();
        this.visualTime.active = mixinOptionsInstance().clockManagerMixin;

        this.hudPositions = Button.builder(Component.translatable("qualityofqueso.gui.hud_positions"), button -> {
            this.minecraft.setScreen(new HudPositionsScreen(this));
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.hud_positions.tooltip"))
        ).build();

        this.debugHuds = Button.builder(Component.translatable("qualityofqueso.gui.debug_huds"), button -> {
            this.minecraft.setScreen(new DebugOptionsScreen(this));
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.debug_huds.tooltip"))
        ).build();

        return new AbstractWidget[]{
                Button.builder(Component.translatable("qualityofqueso.gui.configure"), button -> {
                    if (!MultiLoader.getPlatform().isModLoaded(PlatformMod.YACL)) {
                        this.minecraft.getToasts().addToast(
                                SystemToast.multiline(
                                        this.minecraft,
                                        SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
                                        Component.translatable("qualityofqueso.toast.title.yacl").withStyle(ChatFormatting.RED),
                                        Component.translatable("qualityofqueso.toast.yacl")));
                    } else {
                        this.minecraft.setScreen(ConfigurationScreen.configScreen().generateScreen(this));
                    }
                }).tooltip(Tooltip.create(Component.translatable("qualityofqueso.gui.configure.tooltip"))).build(),

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
                    this.minecraft.setScreen(new ResourcesScreen(this));
                }).tooltip(
                        Tooltip.create(Component.translatable("qualityofqueso.gui.resources.tooltip"))
                ).build(),
        };
    }

    @Override
    protected void activateButtons() {
        boolean modEnabled = modEnabled(this.minecraft);
        this.openItemFrameSearchGUIOptions.active = modEnabled && commonOptionsInstance().itemFrameSearching && this.minecraft.level != null;
        this.visualTime.active = modEnabled;
        this.hudPositions.active = modEnabled && this.minecraft.level != null && (!clientOptionsInstance().getHudOptions().armorStatus.off() || clientOptionsInstance().getItemCounterOptions().itemCounter.enabled());
        this.debugHuds.active = modEnabled;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }
}