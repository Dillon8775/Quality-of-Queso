package net.dillon.qualityofqueso.option.screen;

import net.dillon.qualityofqueso.main.QoQ;
import net.dillon.qualityofqueso.screen.ItemFrameSearchScreen;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ModOptionsScreen extends AbstractModOptionsScreen {
    private ButtonWidget chestSearchingOptions, inventoryManagementOptions, itemFrameSearchingOptions, openItemFrameSearchGUI, hudOptions, miscOptions, reportBugs;

    public ModOptionsScreen(Screen parent) {
        super(parent, Text.translatable("qualityofqueso.gui.options.title"));
    }

    @Override
    protected void init() {
        super.init();
        List<ClickableWidget> buttons = new ArrayList<>();
        this.chestSearchingOptions = this.addDrawableChild(ButtonWidget.builder(Text.translatable("qualityofqueso.gui.inventory_searching_options"), button -> {
            this.client.setScreen(new InventorySearchingOptionsScreen(this));
        }).build());
        buttons.add(this.chestSearchingOptions);
        this.inventoryManagementOptions = this.addDrawableChild(ButtonWidget.builder(Text.translatable("qualityofqueso.gui.inventory_management_options"), button -> {
            this.client.setScreen(new InventoryManagementOptionsScreen(this));
        }).build());
        buttons.add(this.inventoryManagementOptions);

        this.itemFrameSearchingOptions = this.addDrawableChild(ButtonWidget.builder(Text.translatable("qualityofqueso.gui.item_frame_searching_options"), button -> {
            this.client.setScreen(new ItemFrameSearchingOptionsScreen(this));
        }).build());
        buttons.add(this.itemFrameSearchingOptions);
        this.openItemFrameSearchGUI = this.addDrawableChild(ButtonWidget.builder(Text.translatable("qualityofqueso.gui.open_item_frame_search_gui"), button -> {
            if (QoQ.coptions().itemFrameSearching && this.client.world != null) {
                this.client.setScreen(new ItemFrameSearchScreen(this));
            }
        }).build());
        buttons.add(this.openItemFrameSearchGUI);

        this.hudOptions = this.addDrawableChild(ButtonWidget.builder(Text.translatable("qualityofqueso.gui.hud_options"), button -> {
            this.client.setScreen(new HudOptionsScreen(this));
        }).build());
        buttons.add(this.hudOptions);
        this.miscOptions = this.addDrawableChild(ButtonWidget.builder(Text.translatable("qualityofqueso.gui.misc_options"), button -> {
            this.client.setScreen(new MiscOptionsScreen(this));
        }).build());
        buttons.add(this.miscOptions);

        this.reportBugs = this.addDrawableChild(ButtonWidget.builder(Text.translatable("qualityofqueso.gui.report_bugs"),
                ConfirmLinkScreen.opening(this, "https://github.com/Dillon8775/Quality-of-Queso/issues", false)
        ).build());
        buttons.add(this.reportBugs);

        this.body.addAll(buttons);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.openItemFrameSearchGUI.active = QoQ.coptions().itemFrameSearching && this.client.world != null;
        if (this.chestSearchingOptions.isHovered()) {
            ButtonUtil.drawTooltip(Text.translatable("qualityofqueso.gui.chest_searching_options.tooltip"), context, this.textRenderer, mouseX, mouseY);
        }
        if (this.inventoryManagementOptions.isHovered()) {
            ButtonUtil.drawTooltip(Text.translatable("qualityofqueso.gui.inventory_management_options.tooltip"), context, this.textRenderer, mouseX, mouseY);
        }
        if (this.itemFrameSearchingOptions.isHovered()) {
            ButtonUtil.drawTooltip(Text.translatable("qualityofqueso.gui.item_frame_searching_options.tooltip"), context, this.textRenderer, mouseX, mouseY);
        }
        if (this.openItemFrameSearchGUI.isHovered()) {
            if (!QoQ.coptions().itemFrameSearching) {
                ButtonUtil.drawTooltip(Text.translatable("qualityofqueso.gui.open_item_frame_search_gui.disabled"), context, this.textRenderer, mouseX, mouseY);
            } else if (this.client.world == null) {
                ButtonUtil.drawTooltip(Text.translatable("qualityofqueso.gui.open_item_frame_search_gui.null_world"), context, this.textRenderer, mouseX, mouseY);
            }
        }
        if (this.hudOptions.isHovered()) {
            ButtonUtil.drawTooltip(Text.translatable("qualityofqueso.gui.hud_options.tooltip"), context, this.textRenderer, mouseX, mouseY);
        }
        if (QoQ.options().helpfulTooltips) {
            if (this.miscOptions.isHovered()) {
                ButtonUtil.drawTooltip(Text.translatable("qualityofqueso.gui.misc_options.tooltip"), context, this.textRenderer, mouseX, mouseY);
            }
            if (this.reportBugs.isHovered()) {
                ButtonUtil.drawTooltip(Text.translatable("qualityofqueso.gui.report_bugs.tooltip"), context, this.textRenderer, mouseX, mouseY);
            }
        }
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    protected SimpleOption<?>[] options() {
        return new SimpleOption[0];
    }
}