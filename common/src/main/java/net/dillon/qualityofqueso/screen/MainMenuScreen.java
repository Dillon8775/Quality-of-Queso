package net.dillon.qualityofqueso.screen;

import net.dillon.qualityofqueso.config.ConfigurationScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.helper.ModHelper.commonOptionsInstance;
import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;

public class MainMenuScreen extends AbstractModScreen {
    private AbstractWidget openItemFrameSearchGUIOptions, visualTime, debugHuds;

    public MainMenuScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.options.title").withStyle(ChatFormatting.GOLD));
    }

    @Override
    protected void init() {
        super.init();
        AbstractWidget configure = Button.builder(Component.translatable("qualityofqueso.gui.configure"), button -> {
            this.minecraft.setScreen(ConfigurationScreen.configScreen().generateScreen(this));
        }).tooltip(Tooltip.create(Component.translatable("qualityofqueso.gui.configure.tooltip"))).build();

        this.openItemFrameSearchGUIOptions = Button.builder(Component.translatable("qualityofqueso.gui.open_item_frame_search_gui"), button -> {
            if (commonOptionsInstance().itemFrameSearching && this.minecraft.level != null) {
                this.minecraft.setScreen(new ItemFrameSearchScreen(this));
            }
        }).build();

        AbstractWidget keybinds = Button.builder(Component.translatable("qualityofqueso.gui.keybinds"), button -> {
            this.openKeybinds();
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.keybinds.tooltip"))
        ).build();

        this.visualTime = Button.builder(Component.translatable("qualityofqueso.gui.visual_time"), button ->
                this.minecraft.setScreen(new VisualTimeScreen(this))
        ).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.visual_time.description"))
        ).build();

        this.debugHuds = Button.builder(Component.translatable("qualityofqueso.gui.debug_huds"), button -> {
            this.minecraft.setScreen(new DebugOptionsScreen(this));
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.debug_huds.tooltip"))
        ).build();

        GridLayout gridlayout = new GridLayout();
        gridlayout.defaultCellSetting().paddingHorizontal(5).paddingBottom(4).alignHorizontallyCenter();
        GridLayout.RowHelper rowHelper = gridlayout.createRowHelper(2);

        rowHelper.addChild(configure);
        rowHelper.addChild(openItemFrameSearchGUIOptions);
        rowHelper.addChild(keybinds);
        rowHelper.addChild(visualTime);
        rowHelper.addChild(debugHuds);

        gridlayout.arrangeElements();
        FrameLayout.alignInRectangle(gridlayout, 0, this.height / 6 - 12, this.width, this.height, 0.5F, 0.0F);
        gridlayout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    protected void activateButtons() {
        boolean modEnabled = modEnabled(this.minecraft);
        this.openItemFrameSearchGUIOptions.active = modEnabled && commonOptionsInstance().itemFrameSearching && this.minecraft.level != null;
        this.visualTime.active = modEnabled;
        this.debugHuds.active = modEnabled;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    protected OptionInstance<?>[] options() {
        return new OptionInstance[]{
        };
    }

    @Override
    protected boolean addList() {
        return false;
    }
}