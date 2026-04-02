package net.dillon.qualityofqueso.screen;

import net.blay09.mods.balm.Balm;
import net.dillon.qualityofqueso.option.ModListOptions;
import net.dillon.qualityofqueso.packet.GlowSearchC2SPacket;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.dillon.qualityofqueso.util.ModTexts;
import net.dillon.qualityofqueso.util.ModUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import static net.dillon.qualityofqueso.util.ModUtil.options;

/**
 * A utility screen to search for all nearby item frames. If an item frame is found, it glows.
 */
public class ItemFrameSearchScreen extends Screen {
    private EditBox searchField;
    private Button searchButton, clearButton;
    private final Screen parent;

    // Basic constructor; no title text.
    public ItemFrameSearchScreen(@Nullable Screen parent) {
        super(ModTexts.BLANK);
        this.parent = parent;
    }

    /**
     * Initializes the {@code search field} box and constructs the screen.
     */
    @Override
    protected void init() {
        this.searchField = new EditBox(this.font, this.width / 2 - 100, this.height / 2 - 24, 200, 20, Component.empty());
        if (options().searching.saveSearchText) {
            this.searchField.setValue(ModUtil.SAVED_ITEM_FRAME_TEXT);
        }
        this.searchField.setMaxLength(50);
        this.searchButton = this.addRenderableWidget(Button.builder(Component.translatable("qualityofqueso.gui.search"), button -> {
            this.sendPacket(false, options().misc.itemFrameSearchGlowDuration != 0 ? options().misc.itemFrameSearchGlowDuration : 0, options().misc.itemFrameSearchRadius);
        }).bounds(this.width / 2 + 115, this.height / 2 + 24, 100, 20).build());
        AbstractWidget itemFrameSearchRadius = this.addRenderableWidget(ModListOptions.itemFrameSearchRadius().createButton(Minecraft.getInstance().options, this.width / 2 + 5, 20, 100));
        itemFrameSearchRadius.setY(this.height / 2 + 24);
        AbstractWidget itemFrameSearchGlowDuration = this.addRenderableWidget(ModListOptions.itemFrameSearchGlowDuration().createButton(Minecraft.getInstance().options, this.width / 2 - 75, 20, 150));
        itemFrameSearchGlowDuration.setY(itemFrameSearchRadius.getY() + 32);
        this.clearButton = this.addRenderableWidget(Button.builder(Component.translatable("qualityofqueso.gui.clear"), button -> {
            this.clear();
        }).bounds(this.width / 2 - 105, this.height / 2 + 24, 100, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("qualityofqueso.gui.close"), button -> {
            this.close(true);
        }).bounds(this.width / 2 - 215, this.height / 2 + 24, 100, 20).build());
        this.addWidget(this.searchField);
        this.setInitialFocus(this.searchField);
    }

    /**
     * Basic rendering; render the {@code search field}, other texts and tooltips, and basic background.
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        this.searchField.extractWidgetRenderState(graphics, mouseX, mouseY, deltaTicks);
        super.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
        graphics.text(this.font, Component.translatable("qualityofqueso.gui.search_item_frames"), this.width / 2 - 135, this.height / 2 - 110, CommonColors.WHITE);
        graphics.text(this.font, Component.translatable("qualityofqueso.gui.search_item_frames.warning.line1"), this.width / 2 - 175, this.height / 2 - 90, CommonColors.WHITE);
        graphics.text(this.font, Component.translatable("qualityofqueso.gui.search_item_frames.warning.line2"), this.width / 2 - 110, this.height / 2 - 70, CommonColors.WHITE);
        graphics.text(this.font, Component.translatable("qualityofqueso.gui.search_item_frames.line3"), this.width / 2 - 155, this.height / 2 - 50, CommonColors.WHITE);
        this.searchButton.active = !this.searchField.getValue().isEmpty();
        if (!this.searchField.getValue().isEmpty() && this.searchButton.isHovered()) {
            ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.search.tooltip", this.searchField.getValue()), graphics, this.font, mouseX, mouseY);
        }
        if (this.clearButton.isHovered()) {
            ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.clear.tooltip"), graphics, this.font, mouseX, mouseY);
        }
        if (options().accessibility.helpfulTooltips && this.searchField.isHovered() && this.searchField.getValue().isEmpty()) {
            ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.search_item_frames.search_filtering"), graphics, this.font, mouseX, mouseY);
        }
        this.extractBlurredBackground(graphics);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        if (this.minecraft.level == null) {
            this.extractPanorama(graphics, deltaTicks);
        }
        this.extractMenuBackground(graphics);
    }

    /**
     * Handles key pressing.
     */
    @Override
    public boolean keyPressed(KeyEvent input) {
        // Send the packet upon pressing enter.
        if (input.key() == GLFW.GLFW_KEY_ENTER && !this.searchField.getValue().isEmpty()) {
            this.sendPacket(false, options().misc.itemFrameSearchGlowDuration != 0 ? options().misc.itemFrameSearchGlowDuration : 0, options().misc.itemFrameSearchRadius);
        }
        if (Minecraft.getInstance().hasControlDown() && input.key() == GLFW.GLFW_KEY_C) {
            this.clear();
        }
        return super.keyPressed(input);
    }

    /**
     * Proper closing of the screen.
     */
    @Override
    public void onClose() {
        this.close(true);
    }

    /**
     * Properly closes the item frame screen and saves the search field text when closing the screen.
     */
    private void close(boolean backToParent) {
        ModUtil.SAVED_ITEM_FRAME_TEXT = this.searchField.getValue();
        if (backToParent && this.parent != null) {
            this.minecraft.setScreen(this.parent);
        } else {
            super.onClose();
        }
    }

    /**
     * Clears glow from item frames.
     */
    private void clear() {
        this.searchField.setValue("");
        this.sendPacket(true, 0, options().misc.itemFrameSearchRadius);
    }

    /**
     * Closes the screen and sends the glowing packet.
     */
    private void sendPacket(boolean clear, int timer, int radius) {
        this.close(false);
        String text = this.searchField.getValue();
        boolean matchCase = text.startsWith(":");
        Balm.networking().sendToServer(new GlowSearchC2SPacket(text.substring(matchCase ? 1 : 0), matchCase, clear, timer, radius));
    }
}