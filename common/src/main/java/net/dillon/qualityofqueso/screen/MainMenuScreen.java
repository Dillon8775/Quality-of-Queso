package net.dillon.qualityofqueso.screen;

import net.dillon.qualityofqueso.helper.MethodHelper;
import net.dillon.qualityofqueso.keybind.ModKeyMappings;
import net.dillon.qualityofqueso.screen.option.*;
import net.dillon.qualityofqueso.util.MixinPluginUtil;
import net.dillon.qualityofqueso.util.ModTexts;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.debug.DebugOptionsScreen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

import static net.dillon.qualityofqueso.helper.GuiHelper.drawTooltip;
import static net.dillon.qualityofqueso.helper.ModHelper.*;

public class MainMenuScreen extends AbstractModOptionsScreen {
    private AbstractWidget searchingOptions, managementOptions, itemFrameSearchingOptions, openItemFrameSearchGUIOptions, hudOptions, itemCounterOptions, fogOptions, fovEffects, miscOptions, accessibilityOptions, keybinds, clientTime, enableMod, debugHuds, resources;

    public MainMenuScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.options.title").withStyle(ChatFormatting.GOLD));
    }

    @Override
    protected void init() {
        super.init();
        List<AbstractWidget> buttons = new ArrayList<>();
        this.searchingOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.searching_options"), button -> {
            this.minecraft.setScreen(new SearchingOptionsScreen(this));
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.searching_options.tooltip"))
        ).build());
        buttons.add(this.searchingOptions);

        this.managementOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.management_options"), button -> {
            this.minecraft.setScreen(new ManagementOptionsScreen(this));
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.inventory_management_options.tooltip"))
        ).build());
        buttons.add(this.managementOptions);

        this.itemFrameSearchingOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.item_frame_searching_options"), button -> {
            this.minecraft.setScreen(new ItemFrameSearchingOptionsScreen(this));
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.item_frame_searching_options.tooltip"))
        ).build());
        buttons.add(this.itemFrameSearchingOptions);

        this.openItemFrameSearchGUIOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.open_item_frame_search_gui"), button -> {
            if (coptions().itemFrameSearching && this.minecraft.level != null) {
                this.minecraft.setScreen(new ItemFrameSearchScreen(this));
            }
        }).build());
        buttons.add(this.openItemFrameSearchGUIOptions);

        this.hudOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.hud_options"), button -> {
            this.minecraft.setScreen(new HudOptionsScreen(this));
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.hud_options.tooltip"))
        ).build());
        buttons.add(this.hudOptions);

        this.itemCounterOptions = Button.builder(Component.translatable("qualityofqueso.gui.item_counter_options"), button -> {
            this.minecraft.setScreen(new ItemCounterOptionsScreen(this));
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.item_counter_options.tooltip"))
        ).build();
        buttons.add(this.itemCounterOptions);

        this.fovEffects = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.fov_effects"), button -> {
            this.minecraft.setScreen(new FOVEffectsScreen(this));
        }).build());
        buttons.add(this.fovEffects);

        this.fogOptions = Button.builder(Component.translatable("qualityofqueso.gui.fog_options"), button -> {
            this.minecraft.setScreen(new FogOptionsScreen(this));
        }).build();
        buttons.add(this.fogOptions);

        this.clientTime = this.addWidget(Button.builder(
                Component.translatable("qualityofqueso.gui.visual_time"),
                button -> this.minecraft.setScreen(new VisualTimeOptionsScreen(this))
        ).tooltip(Tooltip.create(Component.translatable("qualityofqueso.gui.visual_time.tooltip"))).build());
        buttons.add(this.clientTime);

        this.keybinds = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.keybinds"), button -> {
            this.openKeybinds();
        }).tooltip(Tooltip.create(Component.translatable("qualityofqueso.gui.keybinds.tooltip"))).build());
        buttons.add(this.keybinds);

        this.miscOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.misc_options"), button -> {
            this.minecraft.setScreen(new MiscOptionsScreen(this));
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.misc_options.tooltip")
                )).build());
        buttons.add(this.miscOptions);

        this.accessibilityOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.accessibility_options"), button -> {
            this.minecraft.setScreen(new AccessibilityOptionsScreen(this));
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.accessibility_options.tooltip"))
        ).build());
        buttons.add(this.accessibilityOptions);

        this.enableMod = ListOptions.enableMod().createButton(this.options);
        buttons.add(this.enableMod);

        this.debugHuds = Button.builder(Component.translatable("qualityofqueso.gui.debug_huds"), button -> {
            this.minecraft.setScreen(new DebugOptionsScreen());
            if (Minecraft.getInstance().screen instanceof DebugOptionsScreen debugOptionsScreen) {
                MethodHelper.getDebugScreenSearchBox(debugOptionsScreen).setValue("qualityofqueso");
            }
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.debug_huds.tooltip"))
        ).build();
        buttons.add(this.debugHuds);

        this.resources = Button.builder(Component.translatable("qualityofqueso.gui.resources"), button -> {
            this.minecraft.setScreen(new ResourcesScreen(this));
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.gui.resources.tooltip"))
        ).build();
        buttons.add(this.resources);

        this.list.addSmall(buttons);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
        if (this.openItemFrameSearchGUIOptions.isHovered()) {
            if (!coptions().itemFrameSearching) {
                drawTooltip(Component.translatable("qualityofqueso.gui.open_item_frame_search_gui.disabled"), graphics, this.font, mouseX, mouseY);
            } else if (this.minecraft.level == null) {
                drawTooltip(Component.translatable("qualityofqueso.gui.open_item_frame_search_gui.null_world"), graphics, this.font, mouseX, mouseY);
            } else {
                drawTooltip(Component.translatable("qualityofqueso.options.item_frame_searching.tooltip",
                        ListOptionUtil.kumaKeyMappingAsString(ModKeyMappings.OPEN_SEARCH_ITEM_FRAMES_GUI, false)), graphics, this.font, mouseX, mouseY);
            }
        }

        if (this.fogOptions.isHovered()) {
            drawTooltip(this.functionTooltip(
                    Component.translatable("qualityofqueso.gui.fog_options.tooltip"),
                    Component.translatable("qualityofqueso.gui.fog_options.disabled"),
                    uoptions().functions.applyFog
            ), graphics, this.font, mouseX, mouseY);
        }
        if (this.fovEffects.isHovered()) {
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
    protected AbstractWidget[] options() {
        return new AbstractWidget[]{
        };
    }
}