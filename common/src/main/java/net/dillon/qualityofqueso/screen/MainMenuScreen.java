package net.dillon.qualityofqueso.screen;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.screen.option.*;
import net.dillon.qualityofqueso.util.MixinPluginUtil;
import net.dillon.qualityofqueso.util.ModTexts;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.helper.GuiHelper.drawTooltip;
import static net.dillon.qualityofqueso.helper.ModHelper.*;

public class MainMenuScreen extends AbstractModOptionsScreen {
    private AbstractWidget searchingOptions, managementOptions, itemFrameSearchingOptions, buttonDisplayOptions, openItemFrameSearchGUIOptions, hudOptions, itemCounterOptions, fogOptions, fovEffects, miscOptions, accessibilityOptions, keybinds, clientTime, enableMod, debugHuds;

    public MainMenuScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.options.title").withStyle(ChatFormatting.GOLD));
    }

    @Override
    protected void init() {
        super.init();
        this.searchingOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.searching_options"), button -> {
            this.minecraft.setScreen(new SearchingOptionsScreen(this));
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.searching_options.tooltip"))
        ).build());

        this.managementOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.management_options"), button -> {
            this.minecraft.setScreen(new ManagementOptionsScreen(this));
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.inventory_management_options.tooltip"))
        ).build());

        this.buttonDisplayOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.button_display_options"), button -> {
            this.minecraft.setScreen(new ButtonDisplayOptionsScreen(this));
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.button_display_options.tooltip"))
        ).build());

        this.itemFrameSearchingOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.item_frame_searching_options"), button -> {
            this.minecraft.setScreen(new ItemFrameSearchingOptionsScreen(this));
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.item_frame_searching_options.tooltip"))
        ).build());

        this.openItemFrameSearchGUIOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.open_item_frame_search_gui"), button -> {
            if (coptions().itemFrameSearching && this.minecraft.level != null) {
                this.minecraft.setScreen(new ItemFrameSearchScreen(this));
            }
        }).build());

        this.hudOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.hud_options"), button -> {
            this.minecraft.setScreen(new HudOptionsScreen(this));
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.hud_options.tooltip"))
        ).build());

        this.itemCounterOptions = Button.builder(Component.translatable("qualityofqueso.gui.item_counter_options"), button -> {
            this.minecraft.setScreen(new ItemCounterOptionsScreen(this));
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.item_counter_options.tooltip"))
        ).build();

        this.fovEffects = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.fov_effects"), button -> {
            this.minecraft.setScreen(new FOVEffectsScreen(this));
        }).build());

        this.fogOptions = Button.builder(Component.translatable("qualityofqueso.gui.fog_options"), button -> {
            this.minecraft.setScreen(new FogOptionsScreen(this));
        }).build();

        this.clientTime = this.addWidget(Button.builder(
                Component.translatable("qualityofqueso.gui.visual_time"),
                button -> this.minecraft.setScreen(new VisualTimeOptionsScreen(this))
        ).tooltip(Tooltip.create(Component.translatable("qualityofqueso.gui.visual_time.tooltip"))).build());

        this.keybinds = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.keybinds"), button -> {
            this.openKeybinds();
        }).tooltip(Tooltip.create(Component.translatable("qualityofqueso.gui.keybinds.tooltip"))).build());

        this.miscOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.misc_options"), button -> {
            this.minecraft.setScreen(new MiscOptionsScreen(this));
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.misc_options.tooltip")
                )).build());

        this.accessibilityOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.accessibility_options"), button -> {
            this.minecraft.setScreen(new AccessibilityOptionsScreen(this));
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.accessibility_options.tooltip"))
        ).build());

        this.enableMod = ListOptions.enableMod().createButton(this.options, 0, 0, 150);

        this.debugHuds = Button.builder(Component.translatable("qualityofqueso.gui.debug_huds"), button -> {
            this.minecraft.setScreen(new DebugOptionsScreen(this));
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.debug_huds.tooltip"))
        ).build();

        GridLayout gridlayout = new GridLayout();
        gridlayout.defaultCellSetting().paddingHorizontal(5).paddingBottom(4).alignHorizontallyCenter();
        GridLayout.RowHelper rowHelper = gridlayout.createRowHelper(2);

        rowHelper.addChild(this.searchingOptions);
        rowHelper.addChild(this.managementOptions);
        rowHelper.addChild(this.itemFrameSearchingOptions);
        rowHelper.addChild(this.buttonDisplayOptions);
        rowHelper.addChild(this.hudOptions);
        rowHelper.addChild(this.openItemFrameSearchGUIOptions);
        rowHelper.addChild(this.fogOptions);
        rowHelper.addChild(this.itemCounterOptions);
        rowHelper.addChild(this.fovEffects);
        rowHelper.addChild(this.keybinds);
        rowHelper.addChild(this.miscOptions);
        rowHelper.addChild(this.accessibilityOptions);
        rowHelper.addChild(this.clientTime);
        rowHelper.addChild(this.debugHuds);
        rowHelper.addChild(this.enableMod);

        gridlayout.arrangeElements();
        FrameLayout.alignInRectangle(gridlayout, 0, this.height / 6 - 12, this.width, this.height, 0.5F, 0.0F);
        gridlayout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        super.render(graphics, mouseX, mouseY, deltaTicks);
        if (this.openItemFrameSearchGUIOptions.isMouseOver(mouseX, mouseY)) {
            if (!coptions().itemFrameSearching) {
                drawTooltip(Component.translatable("qualityofqueso.gui.open_item_frame_search_gui.disabled"), graphics, this.font, mouseX, mouseY);
            } else if (this.minecraft.level == null) {
                drawTooltip(Component.translatable("qualityofqueso.gui.open_item_frame_search_gui.null_world"), graphics, this.font, mouseX, mouseY);
            } else {
                drawTooltip(Component.translatable("qualityofqueso.options.item_frame_searching.tooltip",
                        ListOptionUtil.keyMappingAsString(ModKeybinds.OPEN_SEARCH_ITEM_FRAMES_GUI, false)), graphics, this.font, mouseX, mouseY);
            }
        }

        if (this.fogOptions.isMouseOver(mouseX, mouseY)) {
            drawTooltip(this.functionTooltip(
                    Component.translatable("qualityofqueso.gui.fog_options.tooltip"),
                    Component.translatable("qualityofqueso.gui.fog_options.disabled"),
                    uoptions().functions.applyFog
            ), graphics, this.font, mouseX, mouseY);
        }
        if (this.fovEffects.isMouseOver(mouseX, mouseY)) {
            drawTooltip(
                    !MixinPluginUtil.ACTIVE_FOV_EFFECT_BLACKLISTED_MODS.isEmpty()
                            ? Component.translatable("qualityofqueso.gui.feature_disabled_because_mod_present", MixinPluginUtil.ACTIVE_FOV_EFFECT_BLACKLISTED_MODS.toArray())
                            : this.functionTooltip(
                            Component.translatable("qualityofqueso.gui.fov_effects.tooltip"),
                            Component.translatable("qualityofqueso.gui.fov_effects.disabled"),
                            uoptions().functions.applyFovEffects
                    ), graphics, this.font, mouseX, mouseY);
        }

        this.active(this.searchingOptions);
        this.active(this.managementOptions);
        this.active(this.itemFrameSearchingOptions);
        this.active(this.openItemFrameSearchGUIOptions);
        this.active(this.hudOptions);
        this.active(this.itemCounterOptions);
        this.active(this.fovEffects);
        this.active(this.fogOptions);
        this.active(this.clientTime);
        this.active(this.miscOptions);
        this.active(this.accessibilityOptions);
    }

    /**
     * Makes a button inactive if the mod isn't enabled.
     */
    private void active(AbstractWidget widget) {
        if (widget != null) {
            boolean bl = modEnabled(Minecraft.getInstance());
            if (widget.equals(this.openItemFrameSearchGUIOptions)) {
                bl = bl && coptions().itemFrameSearching && this.minecraft.level != null;
            } else if (widget.equals(this.fovEffects)) {
                bl = bl && uoptions().functions.applyFovEffects && MixinPluginUtil.ACTIVE_FOV_EFFECT_BLACKLISTED_MODS.isEmpty();
            } else if (widget.equals(this.fogOptions)) {
                bl = bl && uoptions().functions.applyFog;
            }
            widget.active = bl;
        }
    }

    /**
     * @return a function tooltip.
     */
    private Component functionTooltip(Component enabled, Component disabled, boolean function) {
        Component tooltip = function ? enabled : disabled;
        return tooltip.copy().append(!function ? Component.translatable("qualityofqueso.gui.function_disabled") : ModTexts.BLANK);
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