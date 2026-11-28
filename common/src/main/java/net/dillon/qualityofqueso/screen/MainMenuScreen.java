package net.dillon.qualityofqueso.screen;

import net.dillon.dillonlib.client.component.BasicSpriteIconButtonBuilder;
import net.dillon.dillonlib.client.component.DoneButtonBuilder;
import net.dillon.dillonlib.client.component.GridBuilder;
import net.dillon.dillonlib.client.screen.DillonLibScreen;
import net.dillon.dillonlib.client.util.KeybindScrollHelper;
import net.dillon.dillonlib.task.ClientTasks;
import net.dillon.dillonlib.widespread.DillonsWidgets;
import net.dillon.qualityofqueso.config.ConfigurationScreen;
import net.dillon.qualityofqueso.helper.ButtonHelper;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.helper.ModHelper;
import net.dillon.qualityofqueso.keybind.ModKeyMappings;
import net.dillon.qualityofqueso.widget.OpenVisualTimeGuiButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;

import static net.dillon.dillonlib.client.component.GridBuilder.createRow;
import static net.dillon.dillonlib.task.ClientTasks.*;
import static net.dillon.dillonlib.widespread.DillonsWidgets.createMultiLineText;
import static net.dillon.dillonlib.widespread.DillonsWidgets.finalizeHeaderAndFooter;
import static net.dillon.qualityofqueso.helper.ModConstants.ITEM_FRAME_SEARCH;
import static net.dillon.qualityofqueso.helper.ModConstants.MOD_INFO;
import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;
import static net.dillon.qualityofqueso.option.ClientOptionInstances.client;
import static net.dillon.qualityofqueso.option.ClientOptionInstances.mixins;

public class MainMenuScreen extends DillonLibScreen {

    public MainMenuScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.title").withStyle(ChatFormatting.GOLD));
    }

    @Override
    public void widgets() {
        AbstractWidget configure = createWidget(
                Button.builder(
                        Component.translatable("qualityofqueso.menu.configure"),
                        button -> ClientTasks.tryOpenYaclScreen(
                                () -> ConfigurationScreen.configScreen().generateScreen(this),
                                Component.translatable("qualityofqueso")
                        )
                ).width(208).build()
        );

        AbstractWidget keybinds = createWidget(
                Button.builder(Component.translatable("qualityofqueso.menu.keybinds"), b -> {
                    KeybindScrollHelper.request(ModKeyMappings.QOQ_KEY_CATEGORY);
                    openScreen(new KeyBindsScreen(this, getMinecraft().options));
                }).width(100).build()
        );

        AbstractWidget openItemFrameSearchGUI = createWidget(
                BasicSpriteIconButtonBuilder.builder(
                                ITEM_FRAME_SEARCH,
                                b -> openScreen(new ItemFrameSearchScreen(getScreen()))
                        )
                        .build(),
                ModKeyMappings::canSearchItemFrames,
                isActive -> {
                    if (!isActive) {
                        return Component.translatable("qualityofqueso.item_frame_search_gui.disabled").withStyle(ChatFormatting.RED);
                    }
                    return Component.translatable("qualityofqueso.item_frame_search_gui");
                }
        );

        AbstractWidget visualTime = createWidget(
                new OpenVisualTimeGuiButton(),
                () -> modEnabled() && mixins().clockManagerMixin && !isCurrentLevelNull()
        );

        AbstractWidget hudPositions = createWidget(
                Button.builder(Component.translatable("qualityofqueso.menu.hud_positions"), button -> {
                    openScreen(new HudPositionsScreen(this));
                }).tooltip(
                        Tooltip.create(Component.translatable("qualityofqueso.menu.hud_positions.tooltip"))
                ).width(100).build(),
                () -> modEnabled() && !isCurrentLevelNull() && (!client().hud().armorStatus.off() || client().itemCounter().itemCounter.enabled())
        );

        AbstractWidget debugHuds = createWidget(
                Button.builder(Component.translatable("qualityofqueso.menu.debug_huds"), button -> openDebugEntriesScreen(this, "qualityofqueso")
                ).tooltip(
                        Tooltip.create(Component.translatable("qualityofqueso.menu.debug_huds.tooltip"))
                ).width(100).build(),
                ModHelper::modEnabled
        );

        AbstractWidget packTemplate = createWidget(
                Button.builder(Component.translatable("qualityofqueso.menu.resource_pack_template"),
                        (button) -> openLink(this, ModConstants.RESOURCE_PACK_TEMPLATE, false)
                ).tooltip(
                        Tooltip.create(Component.translatable("qualityofqueso.menu.resource_pack_template.tooltip"))
                ).width(100).build()
        );

        AbstractWidget viewLastKnownEnderChestButton = createWidget(
                ButtonHelper.createViewLastKnownEnderChestButton(),
                ModHelper::modEnabled
        );

        AbstractWidget createCustomItemGroups = createWidget(
                ButtonHelper.createCustomItemGroupsButton(),
                ModHelper::modEnabled
        );

        GridBuilder builder = GridBuilder.create(this)
                .setRowSpacing(4)
                .setColumnSpacing(8);

        builder.fillRow(
                createMultiLineText(Component.translatable("qualityofqueso.title")
                        .copy().withStyle(ChatFormatting.GOLD)),
                builder.alignHorizontallyCenter().paddingBottom(8)
        );

        builder.fillRow(
                createMultiLineText(Component.translatable("qualityofqueso.header.settings")),
                builder.alignHorizontallyCenter().paddingTop(8)
        );

        builder.fillRowAndCenter(configure);
        builder.getCurrentRow().addChild(keybinds);
        builder.getCurrentRow().addChild(hudPositions);

        builder.fillRow(
                createMultiLineText(Component.translatable("qualityofqueso.header.utilities")),
                builder.alignHorizontallyCenter().paddingTop(8)
        );

        LinearLayout utilities = createRow(4);
        utilities.addChild(ButtonHelper.createManageLevelButton());
        utilities.addChild(ButtonHelper.createScreenshotsButton());
        utilities.addChild(visualTime);
        utilities.addChild(openItemFrameSearchGUI);
        utilities.addChild(createCustomItemGroups);
        utilities.addChild(ButtonHelper.createManageContainerDataButton());
        utilities.addChild(viewLastKnownEnderChestButton);
        builder.fillRowAndCenter(utilities);

        builder.getCurrentRow().addChild(debugHuds);
        builder.getCurrentRow().addChild(packTemplate);

        builder.fillRow(
                DillonsWidgets.createVersionLabel(MOD_INFO, this),
                builder.alignHorizontallyCenter().paddingTop(8)
        );

        headerAndFooterLayout.addToFooter(
                DoneButtonBuilder.builder(this)
                        .width(175)
                        .build()
        );
        finalizeHeaderAndFooter(this, headerAndFooterLayout);

        builder.setVerticalAlignment(0.35F).buildAndAlign();
    }
}