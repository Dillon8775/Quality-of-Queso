package net.dillon.qualityofqueso.option.screen;

import net.dillon.qualityofqueso.option.ModListOptions;
import net.dillon.qualityofqueso.screen.ItemFrameSearchScreen;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.dillon.qualityofqueso.util.ModTexts;
import net.dillon.qualityofqueso.util.ModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.LevelResource;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static net.dillon.qualityofqueso.util.ModUtil.coptions;

public class ModOptionsScreen extends AbstractModOptionsScreen {
    private AbstractWidget searchingOptions, inventoryManagementOptions, itemFrameSearchingOptions, openItemFrameSearchGUIOptions, hudOptions, fogOptions, fovEffects, miscOptions, accessibilityOptions, enableMod, showcaseVideo, wiki, askQuestions, reportBugs, openWorldDirectory;

    public ModOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.options.title").withStyle(ChatFormatting.GOLD));
    }

    @Override
    protected void init() {
        super.init();
        List<AbstractWidget> buttons = new ArrayList<>();
        this.searchingOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.searching_options"), button -> {
            this.minecraft.setScreen(new SearchingOptionsScreen(this));
        }).build());
        buttons.add(this.searchingOptions);
        this.inventoryManagementOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.management_options"), button -> {
            this.minecraft.setScreen(new ManagementOptionsScreen(this));
        }).build());
        buttons.add(this.inventoryManagementOptions);

        this.itemFrameSearchingOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.item_frame_searching_options"), button -> {
            this.minecraft.setScreen(new ItemFrameSearchingOptionsScreen(this));
        }).build());
        buttons.add(this.itemFrameSearchingOptions);
        this.openItemFrameSearchGUIOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.open_item_frame_search_gui"), button -> {
            if (coptions().itemFrameSearching && this.minecraft.level != null) {
                this.minecraft.setScreen(new ItemFrameSearchScreen(this));
            }
        }).build());
        buttons.add(this.openItemFrameSearchGUIOptions);

        this.hudOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.hud_options"), button -> {
            this.minecraft.setScreen(new HudOptionsScreen(this));
        }).build());
        buttons.add(this.hudOptions);
        this.fogOptions = Button.builder(Component.translatable("qualityofqueso.gui.fog_options"), button -> {
            this.minecraft.setScreen(new FogOptionsScreen(this));
        }).build();
        this.fogOptions.active = ModUtil.uoptions().functions.applyFog;
        buttons.add(this.fogOptions);

        this.fovEffects = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.fov_effects"), button -> {
            this.minecraft.setScreen(new FOVEffectsScreen(this));
        }).build());
        this.fovEffects.active = ModUtil.uoptions().functions.applyFovEffects;
        buttons.add(this.fovEffects);
        this.miscOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.misc_options"), button -> {
            this.minecraft.setScreen(new MiscOptionsScreen(this));
        }).build());
        buttons.add(this.miscOptions);

        this.accessibilityOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.accessibility_options"), button -> {
            this.minecraft.setScreen(new AccessibilityOptionsScreen(this));
        }).build());
        buttons.add(this.accessibilityOptions);
        this.enableMod = ModListOptions.enableQoQ().createButton(this.options);
        buttons.add(this.enableMod);

        this.showcaseVideo = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.showcase_video"),
                ConfirmLinkScreen.confirmLink(this, ModUtil.SHOWCASE_VIDEO_LINK, false)
        ).build());
        buttons.add(this.showcaseVideo);
        this.wiki = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.wiki"),
            ConfirmLinkScreen.confirmLink(this, ModUtil.WIKI_LINK, false)
        ).build());
        buttons.add(this.wiki);

        this.askQuestions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.ask_questions"),
                ConfirmLinkScreen.confirmLink(this, "https://discord.gg/vfqEAn4YFy", false)
        ).build());
        buttons.add(this.askQuestions);
        this.reportBugs = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.report_bugs"),
                ConfirmLinkScreen.confirmLink(this, "https://github.com/Dillon8775/Quality-of-Queso/issues", false)
        ).build());
        buttons.add(this.reportBugs);

        if (this.minecraft.getSingleplayerServer() != null && this.minecraft.level != null) {
            this.openWorldDirectory = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.open_world_directory"), (button) -> {
                Path worldPath = this.minecraft.getSingleplayerServer().getWorldPath(LevelResource.ROOT);
                Util.getPlatform().openFile(worldPath.toFile());
            }).build());
            buttons.add(this.openWorldDirectory);
        }

        this.list.addSmall(buttons);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
        this.openItemFrameSearchGUIOptions.active = coptions().itemFrameSearching && this.minecraft.level != null;
        if (this.searchingOptions.isHovered()) {
            ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.searching_options.tooltip"), graphics, this.font, mouseX, mouseY);
        }
        if (this.inventoryManagementOptions.isHovered()) {
            ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.inventory_management_options.tooltip"), graphics, this.font, mouseX, mouseY);
        }
        if (this.itemFrameSearchingOptions.isHovered()) {
            ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.item_frame_searching_options.tooltip"), graphics, this.font, mouseX, mouseY);
        }
        if (this.openItemFrameSearchGUIOptions.isHovered()) {
            if (!coptions().itemFrameSearching) {
                ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.open_item_frame_search_gui.disabled"), graphics, this.font, mouseX, mouseY);
            } else if (this.minecraft.level == null) {
                ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.open_item_frame_search_gui.null_world"), graphics, this.font, mouseX, mouseY);
            }
        }
        if (this.hudOptions.isHovered()) {
            ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.hud_options.tooltip"), graphics, this.font, mouseX, mouseY);
        }
        if (ModUtil.options().accessibility.helpfulTooltips) {
            if (this.fogOptions.isHovered()) {
                ButtonUtil.drawTooltip(this.functionTooltip(
                        Component.translatable("qualityofqueso.gui.fog_options.tooltip"),
                        Component.translatable("qualityofqueso.gui.fog_options.disabled"),
                        ModUtil.uoptions().functions.applyFog
                ), graphics, this.font, mouseX, mouseY);
            }
            if (this.fovEffects.isHovered()) {
                ButtonUtil.drawTooltip(this.functionTooltip(
                        Component.translatable("qualityofqueso.gui.fov_effects.tooltip"),
                        Component.translatable("qualityofqueso.gui.fov_effects.disabled"),
                        ModUtil.uoptions().functions.applyFovEffects
                ), graphics, this.font, mouseX, mouseY);
            }
            if (this.miscOptions.isHovered()) {
                ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.misc_options.tooltip"), graphics, this.font, mouseX, mouseY);
            }
            if (this.accessibilityOptions.isHovered()) {
                ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.accessibility_options.tooltip"), graphics, this.font, mouseX, mouseY);
            }
            if (this.showcaseVideo.isHovered()) {
                ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.showcase_video.tooltip"), graphics, this.font, mouseX, mouseY);
            }
            if (this.wiki.isHovered()) {
                ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.wiki.tooltip"), graphics, this.font, mouseX, mouseY);
            }
            if (this.askQuestions.isHovered()) {
                ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.ask_questions.tooltip"), graphics, this.font, mouseX, mouseY);
            }
            if (this.reportBugs.isHovered()) {
                ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.report_bugs.tooltip"), graphics, this.font, mouseX, mouseY);
            }
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
        return new OptionInstance[0];
    }
}