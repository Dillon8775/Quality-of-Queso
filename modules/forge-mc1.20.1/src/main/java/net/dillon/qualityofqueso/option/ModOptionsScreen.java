package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.option.options.InventoryManagementOptionsScreen;
import net.dillon.qualityofqueso.option.options.InventorySearchingOptionsScreen;
import net.dillon.qualityofqueso.option.options.ItemFrameSearchingOptionsScreen;
import net.dillon.qualityofqueso.option.options.MiscOptionsScreen;
import net.dillon.qualityofqueso.screen.ItemFrameSearchScreen;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModOptionsScreen extends AbstractModOptionsScreen {
    private Button chestSearchingOptions, inventoryManagementOptions, itemFrameSearchingOptions, openItemFrameSearchGUIOptions, otherOptions, reportBugs;

    public ModOptionsScreen(Screen parent) {
        super(parent, Component.translatable("qualityofqueso.gui.options.title"));
    }

    @Override
    protected void init() {
        super.init();
        this.chestSearchingOptions = Button.builder(Component.translatable("qualityofqueso.gui.inventory_searching_options"), button -> {
            this.minecraft.setScreen(new InventorySearchingOptionsScreen(this));
        }).build();
        this.inventoryManagementOptions = Button.builder(Component.translatable("qualityofqueso.gui.inventory_management_options"), button -> {
            this.minecraft.setScreen(new InventoryManagementOptionsScreen(this));
        }).build();
        this.itemFrameSearchingOptions = Button.builder(Component.translatable("qualityofqueso.gui.item_frame_searching_options"), button -> {
            this.minecraft.setScreen(new ItemFrameSearchingOptionsScreen(this));
        }).build();
        this.openItemFrameSearchGUIOptions = Button.builder(Component.translatable("qualityofqueso.gui.open_item_frame_search_gui"), button -> {
            if (ModCommonOptions.ITEM_FRAME_SEARCHING.get() && this.minecraft.level != null) {
                this.minecraft.setScreen(new ItemFrameSearchScreen());
            }
        }).build();
        this.otherOptions = Button.builder(Component.translatable("qualityofqueso.gui.misc_options"), button -> {
            this.minecraft.setScreen(new MiscOptionsScreen(this));
        }).build();
        this.reportBugs = this.addWidget(Button.builder(Component.translatable("qualityofqueso.gui.report_bugs"),
                ConfirmLinkScreen.confirmLink("https://github.com/Dillon8775/Quality-of-Queso/issues", this , false)
        ).build());

        GridLayout gridlayout = new GridLayout();
        gridlayout.defaultCellSetting().paddingHorizontal(5).paddingBottom(4).alignHorizontallyCenter();
        GridLayout.RowHelper rowHelper = gridlayout.createRowHelper(2);

        rowHelper.addChild(this.chestSearchingOptions);
        rowHelper.addChild(this.inventoryManagementOptions);
        rowHelper.addChild(this.itemFrameSearchingOptions);
        rowHelper.addChild(this.openItemFrameSearchGUIOptions);
        rowHelper.addChild(this.otherOptions);
        rowHelper.addChild(this.reportBugs);

        gridlayout.arrangeElements();
        FrameLayout.alignInRectangle(gridlayout, 0, this.height / 6 - 12, this.width, this.height, 0.5F, 0.0F);
        gridlayout.visitWidgets(this::addRenderableWidget);
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

    @Override
    protected boolean addList() {
        return false;
    }
}