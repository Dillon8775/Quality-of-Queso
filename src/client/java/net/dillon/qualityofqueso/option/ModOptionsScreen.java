package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.QualityOfQuesoClient;
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
        super(parent, Text.translatable(QualityOfQuesoClient.TITLE));
    }

    @Override
    protected void init() {
        super.init();
        List<ClickableWidget> buttons = new ArrayList<>();
        this.chestSearchingOptions = this.addDrawableChild(ButtonWidget.builder(Text.translatable("qualityofqueso.gui.chest_searching_options"), button -> {
            this.client.setScreen(new AbstractModOptionsScreen(this, Text.translatable("qualityofqueso.gui.chest_searching_options")) {
                @Override
                protected SimpleOption<?>[] options() {
                    return new SimpleOption[]{
                            ModListOptions.CHEST_SEARCH,
                            ModListOptions.SEARCH_INVENTORY,
                            ModListOptions.SAVE_SEARCH_TEXT
                    };
                }
            });
        }).build());
        buttons.add(this.chestSearchingOptions);
        this.inventorySortingOptions = this.addDrawableChild(ButtonWidget.builder(Text.translatable("qualityofqueso.gui.inventory_sorting_options"), button -> {
            this.client.setScreen(new AbstractModOptionsScreen(this, Text.translatable("qualityofqueso.gui.inventory_sorting_options")) {
                @Override
                protected SimpleOption<?>[] options() {
                    return new SimpleOption[]{
                            ModListOptions.INVENTORY_SORTING,
                            ModListOptions.REQUIRE_ALT_TO_SORT
                    };
                }
            });
        }).build());
        buttons.add(this.inventorySortingOptions);
        this.itemFrameSearchingOptions = this.addDrawableChild(ButtonWidget.builder(Text.translatable("qualityofqueso.gui.item_frame_searching_options"), button -> {
            this.client.setScreen(new AbstractModOptionsScreen(this, Text.translatable("qualityofqueso.gui.item_frame_searching_options")) {
                @Override
                protected SimpleOption<?>[] options() {
                    return new SimpleOption[]{
                            ModListOptions.ITEM_FRAME_SEARCHING,
                            ModListOptions.ITEM_FRAME_SEARCH_TIMER,
                            ModListOptions.ITEM_FRAME_SEARCH_RADIUS
                    };
                }
            });
        }).build());
        buttons.add(this.itemFrameSearchingOptions);
        this.openItemFrameSearchGUI = this.addDrawableChild(ButtonWidget.builder(Text.translatable("qualityofqueso.gui.open_item_frame_search_gui"), button -> {
            if (QualityOfQuesoClient.options().itemFrameSearching && this.client.world != null) {
                this.client.setScreen(new ItemFrameSearchScreen());
            }
        }).build());
        buttons.add(this.openItemFrameSearchGUI);
        this.otherOptions = this.addDrawableChild(ButtonWidget.builder(Text.translatable("qualityofqueso.gui.other_options"), button -> {
            this.client.setScreen(new AbstractModOptionsScreen(this, Text.translatable("qualityofqueso.gui.other_options")) {
                @Override
                protected SimpleOption<?>[] options() {
                    return new SimpleOption[]{
                            ModListOptions.BETTER_SEARCHING,
                            ModListOptions.BETTER_GUI_EXIT,
                            ModListOptions.QUICK_EQUIP,
                            ModListOptions.SHOW_CONFIG_BUTTON
                    };
                }
            });
        }).build());
        buttons.add(this.otherOptions);
        this.body.addAll(buttons);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.openItemFrameSearchGUI.active = QualityOfQuesoClient.options().itemFrameSearching && this.client.world != null;
        if (this.chestSearchingOptions.isHovered()) {
            context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(Text.translatable("qualityofqueso.gui.chest_searching_options.tooltip"), 200), mouseX, mouseY);
        }
        if (this.inventorySortingOptions.isHovered()) {
            context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(Text.translatable("qualityofqueso.gui.inventory_sorting_options.tooltip"), 200), mouseX, mouseY);
        }
        if (this.itemFrameSearchingOptions.isHovered()) {
            context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(Text.translatable("qualityofqueso.gui.item_frame_searching_options.tooltip"), 200), mouseX, mouseY);
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