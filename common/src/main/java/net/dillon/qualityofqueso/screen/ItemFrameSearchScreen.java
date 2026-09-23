package net.dillon.qualityofqueso.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.Balm;
import net.dillon.dillonlib.screen.BasicDillonLibScreen;
import net.dillon.dillonlib.screen.ConditionalTooltip;
import net.dillon.dillonlib.util.Texts;
import net.dillon.qualityofqueso.packet.serverbound.GlowSearchC2SPacket;
import net.dillon.qualityofqueso.util.ListOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import static net.dillon.dillonlib.task.ClientTasks.openScreen;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.updateClient;

/**
 * A utility screen to search for all nearby item frames. If an item frame is found, it glows.
 */
public class ItemFrameSearchScreen extends BasicDillonLibScreen {
    private EditBox searchField;
    private final Screen parent;

    public ItemFrameSearchScreen(@Nullable Screen parent) {
        super(Texts.BLANK);
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
        if (this.minecraft.level == null) {
            this.extractPanorama(graphics, deltaTicks);
        }

        this.extractMenuBackground(graphics);
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if (input.key() == InputConstants.KEY_RETURN && !isSearchFieldTextEmpty()) {
            this.sendPacket(false, client().misc().itemFrameSearchGlowDuration != 0 ? client().misc().itemFrameSearchGlowDuration : 0, client().misc().itemFrameSearchRadius);
        }

        if (Minecraft.getInstance().hasControlDown() && input.key() == InputConstants.KEY_C) {
            this.clear();
        }

        return super.keyPressed(input);
    }

    /**
     * @return the search field.
     */
    private EditBox searchField() {
        EditBox searchField = new EditBox(this.font, builder().captureWidth(), builder().captureHeight(), 200, 20, Component.empty());
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
        return this.createWidget(
                Button.builder(Component.translatable("qualityofqueso.gui.search"), button -> {
                            this.sendPacket(false, client().misc().itemFrameSearchGlowDuration != 0 ? client().misc().itemFrameSearchGlowDuration : 0, client().misc().itemFrameSearchRadius);
                        })
                        .bounds(builder().captureWidth(), builder().captureHeight(), 100, 20)
                        .build(),
                () -> !isSearchFieldTextEmpty(),
                () -> ConditionalTooltip.of(
                        Component.translatable("qualityofqueso.gui.search.tooltip", this.searchField.getValue()),
                        Component.empty()
                )
        );
    }

    /**
     * @return the clear button.
     */
    private AbstractWidget clearButton() {
        return this.createWidget(
                Button.builder(Component.translatable("qualityofqueso.gui.clear"), button -> {
                            this.clear();
                        })
                        .bounds(builder().captureWidth(), builder().captureHeight(), 100, 20)
                        .build(),
                Component.translatable("qualityofqueso.gui.clear.tooltip")
        );
    }

    /**
     * @return the close button.
     */
    private AbstractWidget closeButton() {
        return this.createWidget(
                Button.builder(Component.translatable("qualityofqueso.gui.close"), button -> {
                            this.close(true);
                        })
                        .bounds(builder().captureWidth(), builder().captureHeight(), 100, 20)
                        .build()
        );
    }

    @Override
    public void widgets() {
        builder().widthCenter().apply();
        builder().heightCenter().apply();

        builder().heightUp(10).apply();
        builder().widthLeft(100).apply();

        this.searchField = searchField();

        builder().heightDown(36).apply();
        builder().widthCenter().apply();
        builder().widthRight(115).apply();

        this.addRenderableWidget(searchButton());

        builder().widthLeft(110).apply();

        this.addRenderableWidget(
                ListOptions.itemFrameSearchRadius().createButton(
                        Minecraft.getInstance().options,
                        builder().captureWidth(),
                        builder().captureHeight(),
                        100
                )
        );

        builder().widthLeft(110).apply();

        this.addRenderableWidget(clearButton());

        builder().widthLeft(110).apply();

        this.addRenderableWidget(closeButton());

        builder().heightDown(32).apply();
        builder().widthCenter().apply();
        builder().widthLeft(75).apply();

        this.addRenderableWidget(
                ListOptions.itemFrameSearchGlowDuration().createButton(
                        Minecraft.getInstance().options,
                        builder().captureWidth(),
                        builder().captureHeight(),
                        150
                )
        );

        this.addRenderableWidget(
                this.createWidget(
                        this.searchField,
                        () -> ConditionalTooltip.ofCondition(
                                () -> client().general().tooltips.enabled(),
                                Component.translatable("qualityofqueso.gui.search_item_frames.search_filtering"),
                                Component.empty()
                        )
                )
        );
        this.setInitialFocus(this.searchField);
    }

    @Override
    protected void drawGraphics(GuiGraphicsExtractor graphics) {
        builder().renderHeightCenter().apply();
        builder().renderHeightUp(96).apply();

        builder().textCenterAndHeightDown(graphics, Component.translatable("qualityofqueso.gui.search_item_frames.line1")).apply();
        builder().textCenterAndHeightDown(graphics, Component.translatable("qualityofqueso.gui.search_item_frames.warning.line1")).apply();
        builder().textCenterAndHeightDown(graphics, Component.translatable("qualityofqueso.gui.search_item_frames.warning.line2")).apply();
        builder().textCenterAndHeightDown(graphics, Component.translatable("qualityofqueso.gui.search_item_frames.line2")).apply();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, deltaTicks);

        this.extractBlurredBackground(graphics);
    }
}