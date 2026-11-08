package net.dillon.qualityofqueso.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.Balm;
import net.dillon.dillonlib.client.component.GridBuilder;
import net.dillon.dillonlib.client.screen.DillonLibScreen;
import net.dillon.dillonlib.widespread.DillonsTexts;
import net.dillon.qualityofqueso.packet.serverbound.GlowSearchC2SPacket;
import net.dillon.qualityofqueso.util.ListOptions;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import static net.dillon.dillonlib.client.component.GridBuilder.createRow;
import static net.dillon.dillonlib.task.ClientTasks.*;
import static net.dillon.dillonlib.widespread.DillonsWidgets.createMultiLineText;
import static net.dillon.dillonlib.widespread.DillonsWidgets.createOption;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.updateClient;

/**
 * A utility screen to search for all nearby item frames. If an item frame is found, it glows.
 */
public class ItemFrameSearchScreen extends DillonLibScreen {
    private EditBox searchField;
    private final Screen parent;

    public ItemFrameSearchScreen(@Nullable Screen parent) {
        super(DillonsTexts.BLANK);
        this.parent = parent;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        this.close(true);
    }

    /**
     * Handles closing the screen.
     */
    private void close(boolean backToParent) {
        updateClient(client -> client.searching().savedItemFrameSearchText = this.searchField.getValue());
        if (backToParent && this.parent != null) {
            openScreen(this.parent);
        } else {
            super.onClose();
        }
    }

    /**
     * Sends the packet to clear all glow from item frames.
     */
    private void clear() {
        this.searchField.setValue("");
        this.sendPacket(true, 0, client().misc().itemFrameSearchRadius);
    }

    /**
     * Sends the packet to handle glow item frames.
     */
    private void sendPacket(boolean clear, int timer, int radius) {
        this.close(false);
        String text = this.searchField.getValue();
        boolean matchCase = text.startsWith(":");
        Balm.networking().sendToServer(new GlowSearchC2SPacket(text.substring(matchCase ? 1 : 0), matchCase, clear, timer, radius));
    }

    /**
     * @return if the search fields text is empty.
     */
    private boolean isSearchFieldTextEmpty() {
        return this.searchField.getValue().isEmpty();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        if (isCurrentLevelNull()) {
            this.extractPanorama(graphics, deltaTicks);
        }

        this.extractMenuBackground(graphics);
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if (input.key() == InputConstants.KEY_RETURN && !isSearchFieldTextEmpty()) {
            this.sendPacket(false, client().misc().itemFrameSearchGlowDuration != 0 ? client().misc().itemFrameSearchGlowDuration : 0, client().misc().itemFrameSearchRadius);
        }

        if (hasCtrlDown() && input.key() == InputConstants.KEY_C) {
            this.clear();
        }

        return super.keyPressed(input);
    }

    /**
     * @return the search field.
     */
    private EditBox searchField() {
        EditBox searchField = new EditBox(this.font, 200, 20, Component.empty());
        if (client().searching().saveSearchText) {
            searchField.setValue(client().searching().savedItemFrameSearchText);
        }

        searchField.setMaxLength(50);
        return searchField;
    }

    /**
     * @return the search button.
     */
    private AbstractWidget searchButton() {
        return createWidget(
                Button.builder(Component.translatable("qualityofqueso.gui.search"), button -> {
                            this.sendPacket(false, client().misc().itemFrameSearchGlowDuration != 0 ? client().misc().itemFrameSearchGlowDuration : 0, client().misc().itemFrameSearchRadius);
                        })
                        .size(100, 20)
                        .build(),
                () -> !isSearchFieldTextEmpty(),
                isActive -> {
                    if (!isActive) {
                        return Component.empty();
                    }

                    return Component.translatable("qualityofqueso.gui.search.tooltip", this.searchField.getValue());
                }
        );
    }

    /**
     * @return the clear button.
     */
    private AbstractWidget clearButton() {
        return createWidget(
                Button.builder(Component.translatable("qualityofqueso.gui.clear"), button -> {
                            this.clear();
                        })
                        .size(100, 20)
                        .build(),
                Component.translatable("qualityofqueso.gui.clear.tooltip")
        );
    }

    /**
     * @return the close button.
     */
    private AbstractWidget closeButton() {
        return createWidget(
                Button.builder(Component.translatable("qualityofqueso.gui.close"), button -> {
                            this.close(true);
                        })
                        .size(100, 20)
                        .build()
        );
    }

    @Override
    public void widgets() {
        GridBuilder builder = GridBuilder.create(this)
                .setRowSpacing(16)
                .setColumnSpacing(16);

        MultiLineTextWidget text = createMultiLineText(Component.translatable("qualityofqueso.gui.search_item_frames"));
        builder.fillRowAndCenter(text);

        this.searchField = searchField();
        builder.fillRowAndCenter(
                createWidget(
                        this.searchField,
                        isActive -> {
                            if (client().general().tooltips.enabled()) {
                                return Component.translatable("qualityofqueso.gui.search_item_frames.search_filtering");
                            } else {
                                return Component.empty();
                            }
                        }
                )
        );

        LinearLayout controls = createRow();
        controls.addChild(closeButton());
        controls.addChild(clearButton());
        controls.addChild(
                createOption(
                        ListOptions.itemFrameSearchRadius(),
                        100
                )
        );
        controls.addChild(searchButton());
        builder.fillRowAndCenter(controls);

        builder.fillRowAndCenter(
                createOption(
                        ListOptions.itemFrameSearchGlowDuration(),
                        150
                )
        );

        builder.setVerticalAlignment(0.45F).buildAndAlign();
        this.setInitialFocus(this.searchField);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
        this.extractBlurredBackground(graphics);
    }
}