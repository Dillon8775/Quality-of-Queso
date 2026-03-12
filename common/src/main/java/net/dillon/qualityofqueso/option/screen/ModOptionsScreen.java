package net.dillon.qualityofqueso.option.screen;

import net.dillon.qualityofqueso.option.ModListOptions;
import net.dillon.qualityofqueso.screen.ItemFrameSearchScreen;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.dillon.qualityofqueso.util.ModUtil;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

import static net.dillon.qualityofqueso.util.ModUtil.coptions;

public class ModOptionsScreen extends AbstractModOptionsScreen {
    private AbstractWidget searchingOptions, inventoryManagementOptions, itemFrameSearchingOptions, openItemFrameSearchGUIOptions, hudOptions, otherOptions, fovEffects, accessibilityOptions, enableMod, reportBugs, joinDiscord;

    public ModOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.options.title"));
    }

    @Override
    protected void init() {
        super.init();
        List<AbstractWidget> buttons = new ArrayList<>();
        this.searchingOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.searching_options"), button -> {
            this.minecraft.setScreen(new SearchingOptionsScreen(this));
        }).build());
        buttons.add(this.searchingOptions);
        this.inventoryManagementOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.inventory_management_options"), button -> {
            this.minecraft.setScreen(new InventoryManagementOptionsScreen(this));
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
        this.otherOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.misc_options"), button -> {
            this.minecraft.setScreen(new MiscOptionsScreen(this));
        }).build());
        buttons.add(this.otherOptions);

        this.accessibilityOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.accessibility_options"), button -> {
            this.minecraft.setScreen(new AccessibilityOptionsScreen(this));
        }).build());
        buttons.add(this.accessibilityOptions);
        this.enableMod = ModListOptions.enableQoQ().createButton(this.options);
        buttons.add(this.enableMod);

        this.fovEffects = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.fov_effects"), button -> {
            this.minecraft.setScreen(new FOVEffectsScreen(this));
        }).build());
        buttons.add(this.fovEffects);
        this.joinDiscord = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.ask_questions"),
                ConfirmLinkScreen.confirmLink(this, "https://discord.gg/vfqEAn4YFy", false)
        ).build());
        buttons.add(this.joinDiscord);

        this.reportBugs = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.report_bugs"),
                ConfirmLinkScreen.confirmLink(this, "https://github.com/Dillon8775/Quality-of-Queso/issues", false)
        ).build());
        buttons.add(this.reportBugs);

        this.list.addSmall(buttons);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        super.render(graphics, mouseX, mouseY, deltaTicks);
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
        if (this.fovEffects.isHovered()) {
            ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.fov_effects.tooltip"), graphics, this.font, mouseX, mouseY);
        }
        if (this.accessibilityOptions.isHovered()) {
            ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.accessibility_options.tooltip"), graphics, this.font, mouseX, mouseY);
        }
        if (ModUtil.options().accessibility.helpfulTooltips) {
            if (this.otherOptions.isHovered()) {
                ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.misc_options.tooltip"), graphics, this.font, mouseX, mouseY);
            }
            if (this.reportBugs.isHovered()) {
                ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.report_bugs.tooltip"), graphics, this.font, mouseX, mouseY);
            }
        }
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