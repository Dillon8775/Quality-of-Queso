package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.option.options.InventoryManagementOptionsScreen;
import net.dillon.qualityofqueso.option.options.InventorySearchingOptionsScreen;
import net.dillon.qualityofqueso.option.options.ItemFrameSearchingOptionsScreen;
import net.dillon.qualityofqueso.option.options.MiscOptionsScreen;
import net.dillon.qualityofqueso.screen.ItemFrameSearchScreen;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ModOptionsScreen extends AbstractModOptionsScreen {
    private Button chestSearchingOptions, inventoryManagementOptions, itemFrameSearchingOptions, openItemFrameSearchGUIOptions, otherOptions, reportBugs;

    public ModOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.options.title"));
    }

    @Override
    protected void init() {
        super.init();
        List<AbstractWidget> buttons = new ArrayList<>();
        this.chestSearchingOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.inventory_searching_options"), button -> {
            this.minecraft.setScreen(new InventorySearchingOptionsScreen(this));
        }).build());
        buttons.add(this.chestSearchingOptions);
        this.inventoryManagementOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.inventory_management_options"), button -> {
            this.minecraft.setScreen(new InventoryManagementOptionsScreen(this));
        }).build());
        buttons.add(this.inventoryManagementOptions);
        this.itemFrameSearchingOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.item_frame_searching_options"), button -> {
            this.minecraft.setScreen(new ItemFrameSearchingOptionsScreen(this));
        }).build());
        buttons.add(this.itemFrameSearchingOptions);
        this.openItemFrameSearchGUIOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.open_item_frame_search_gui"), button -> {
            if (ModCommonOptions.ITEM_FRAME_SEARCHING.get() && this.minecraft.level != null) {
                this.minecraft.setScreen(new ItemFrameSearchScreen());
            }
        }).build());
        buttons.add(this.openItemFrameSearchGUIOptions);
        this.otherOptions = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.misc_options"), button -> {
            this.minecraft.setScreen(new MiscOptionsScreen(this));
        }).build());
        buttons.add(this.otherOptions);
        this.reportBugs = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.report_bugs"),
                ConfirmLinkScreen.confirmLink(this, "https://github.com/Dillon8775/Quality-of-Queso/issues", false)
        ).build());
        buttons.add(this.reportBugs);
        this.list.addSmall(buttons);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        super.render(graphics, mouseX, mouseY, deltaTicks);
        this.openItemFrameSearchGUIOptions.active = ModCommonOptions.ITEM_FRAME_SEARCHING.get() && this.minecraft.level != null;
        if (this.chestSearchingOptions.isHovered()) {
            ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.chest_searching_options.tooltip"), graphics, this.font, mouseX, mouseY);
        }
        if (this.inventoryManagementOptions.isHovered()) {
            ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.inventory_management_options.tooltip"), graphics, this.font, mouseX, mouseY);
        }
        if (this.itemFrameSearchingOptions.isHovered()) {
            ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.item_frame_searching_options.tooltip"), graphics, this.font, mouseX, mouseY);
        }
        if (this.openItemFrameSearchGUIOptions.isHovered()) {
            if (!ModCommonOptions.ITEM_FRAME_SEARCHING.get()) {
                ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.open_item_frame_search_gui.disabled"), graphics, this.font, mouseX, mouseY);
            } else if (this.minecraft.level == null) {
                ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.open_item_frame_search_gui.null_world"), graphics, this.font, mouseX, mouseY);
            }
        }
        if (ModClientOptions.HELPFUL_TOOLTIPS.get()) {
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