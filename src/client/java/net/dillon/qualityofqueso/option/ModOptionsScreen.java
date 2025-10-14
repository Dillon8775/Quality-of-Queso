package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.main.QualityOfQueso;
import net.dillon.qualityofqueso.option.options.ChestSearchingOptionsScreen;
import net.dillon.qualityofqueso.option.options.InventorySortingOptionsScreen;
import net.dillon.qualityofqueso.option.options.ItemFrameSearchingOptionsScreen;
import net.dillon.qualityofqueso.option.options.OtherOptionsScreen;
import net.dillon.qualityofqueso.screen.ItemFrameSearchScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ModOptionsScreen extends AbstractModOptionsScreen {
    private ButtonWidget chestSearchingOptions, inventorySortingOptions, itemFrameSearchingOptions, openItemFrameSearchGUI, otherOptions;

    public ModOptionsScreen(Screen parent) {
        super(parent, Text.translatable("qualityofqueso.gui.options.title"));
    }

    @Override
    protected void init() {
        super.init();
        List<ClickableWidget> buttons = new ArrayList<>();
        this.chestSearchingOptions = this.addDrawableChild(ButtonWidget.builder(Text.translatable("qualityofqueso.gui.chest_searching_options"), button -> {
            this.client.setScreen(new ChestSearchingOptionsScreen(this));
        }).build());
        buttons.add(this.chestSearchingOptions);
        this.inventorySortingOptions = this.addDrawableChild(ButtonWidget.builder(Text.translatable("qualityofqueso.gui.inventory_sorting_options"), button -> {
            this.client.setScreen(new InventorySortingOptionsScreen(this));
        }).build());
        buttons.add(this.inventorySortingOptions);
        this.itemFrameSearchingOptions = this.addDrawableChild(ButtonWidget.builder(Text.translatable("qualityofqueso.gui.item_frame_searching_options"), button -> {
            this.client.setScreen(new ItemFrameSearchingOptionsScreen(this));
        }).build());
        buttons.add(this.itemFrameSearchingOptions);
        this.openItemFrameSearchGUI = this.addDrawableChild(ButtonWidget.builder(Text.translatable("qualityofqueso.gui.open_item_frame_search_gui"), button -> {
            if (QualityOfQueso.options().itemFrameSearching && this.client.world != null) {
                this.client.setScreen(new ItemFrameSearchScreen());
            }
        }).build());
        buttons.add(this.openItemFrameSearchGUI);
        this.otherOptions = this.addDrawableChild(ButtonWidget.builder(Text.translatable("qualityofqueso.gui.other_options"), button -> {
            this.client.setScreen(new OtherOptionsScreen(this));
        }).build());
        buttons.add(this.otherOptions);
        this.body.addAll(buttons);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.openItemFrameSearchGUI.active = QualityOfQueso.options().itemFrameSearching && this.client.world != null;
        if (this.chestSearchingOptions.isHovered()) {
            context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(Text.translatable("qualityofqueso.gui.chest_searching_options.tooltip"), 200), mouseX, mouseY);
        }
        if (this.inventorySortingOptions.isHovered()) {
            context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(Text.translatable("qualityofqueso.gui.inventory_sorting_options.tooltip"), 200), mouseX, mouseY);
        }
        if (this.itemFrameSearchingOptions.isHovered()) {
            context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(Text.translatable("qualityofqueso.gui.item_frame_searching_options.tooltip"), 200), mouseX, mouseY);
        }
        if (this.openItemFrameSearchGUI.isHovered()) {
            if (!QualityOfQueso.options().itemFrameSearching) {
                context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(Text.translatable("qualityofqueso.gui.open_item_frame_search_gui.disabled"), 200), mouseX, mouseY);
            } else if (this.client.world == null) {
                context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(Text.translatable("qualityofqueso.gui.open_item_frame_search_gui.null_world"), 200), mouseX, mouseY);
            }
        }
        if (this.otherOptions.isHovered()) {
            context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(Text.translatable("qualityofqueso.gui.other_options.tooltip"), 200), mouseX, mouseY);
        }
    }

    @Override
    protected SimpleOption<?>[] options() {
        return new SimpleOption[0];
    }
}