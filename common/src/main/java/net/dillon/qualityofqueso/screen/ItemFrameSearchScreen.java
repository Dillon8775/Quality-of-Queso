package net.dillon.qualityofqueso.screen;

import net.blay09.mods.balm.api.Balm;
import net.dillon.qualityofqueso.packet.GlowSearchC2SPacket;
import net.dillon.qualityofqueso.screen.option.ListOptions;
import net.dillon.qualityofqueso.util.ModTexts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import org.lwjgl.glfw.GLFW;

import static net.dillon.qualityofqueso.helper.GuiHelper.drawTooltip;
import static net.dillon.qualityofqueso.helper.ModHelper.options;
import static net.dillon.qualityofqueso.util.ModConstants.SAVED_ITEM_FRAME_TEXT;

/**
 * A utility screen to search for all nearby item frames. If an item frame is found, it glows.
 */
public class ItemFrameSearchScreen extends Screen {
    private EditBox searchField;
    private Button searchButton, clearButton;
    private final Screen parent;

    public ItemFrameSearchScreen(Screen parent) {
        super(ModTexts.BLANK);
        this.parent = parent;
    }

    private void clear() {
        this.searchField.setValue("");
        this.sendPacket(true, 0, options().misc.itemFrameSearchRadius);
    }

    private void sendPacket(boolean clear, int timer, int radius) {
        this.close(false);
        String text = this.searchField.getValue();
        boolean matchCase = text.startsWith(":");
        Balm.getNetworking().sendToServer(new GlowSearchC2SPacket(text.substring(matchCase ? 1 : 0), matchCase, clear, timer, radius));
    }

    @Override
    public void onClose() {
        this.close(true);
    }

    private void close(boolean backToParent) {
        SAVED_ITEM_FRAME_TEXT = this.searchField.getValue();
        if (backToParent && this.parent != null) {
            this.minecraft.setScreen(this.parent);
        } else {
            super.onClose();
        }
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        if (this.minecraft.level == null) {
            this.renderPanorama(graphics, deltaTicks);
        }
        this.renderMenuBackground(graphics);
    }

    @Override
    public boolean keyPressed(int keycode, int scancode, int modifiers) {
        // Send the packet upon pressing enter.
        if (keycode == GLFW.GLFW_KEY_ENTER && !this.searchField.getValue().isEmpty()) {
            this.sendPacket(false, options().misc.itemFrameSearchGlowDuration != 0 ? options().misc.itemFrameSearchGlowDuration : 0, options().misc.itemFrameSearchRadius);
        }
        if (Screen.hasControlDown() && keycode == GLFW.GLFW_KEY_C) {
            this.clear();
        }
        return super.keyPressed(keycode, scancode, modifiers);
    }

    @Override
    protected void init() {
        this.searchField = new EditBox(this.font, this.width / 2 - 100, this.height / 2 - 24, 200, 20, Component.empty());
        if (options().searching.saveSearchText) {
            this.searchField.setValue(SAVED_ITEM_FRAME_TEXT);
        }
        this.searchField.setMaxLength(50);
        this.searchButton = this.addRenderableWidget(Button.builder(Component.translatable("qualityofqueso.gui.search"), button -> {
            this.sendPacket(false, options().misc.itemFrameSearchGlowDuration != 0 ? options().misc.itemFrameSearchGlowDuration : 0, options().misc.itemFrameSearchRadius);
        }).bounds(this.width / 2 + 115, this.height / 2 + 24, 100, 20).build());
        AbstractWidget itemFrameSearchRadius = this.addRenderableWidget(ListOptions.itemFrameSearchRadius().createButton(Minecraft.getInstance().options, this.width / 2 + 5, 20, 100));
        itemFrameSearchRadius.setY(this.height / 2 + 24);
        AbstractWidget itemFrameSearchGlowDuration = this.addRenderableWidget(ListOptions.itemFrameSearchGlowDuration().createButton(Minecraft.getInstance().options, this.width / 2 - 75, 20, 150));
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

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        this.searchField.render(graphics, mouseX, mouseY, deltaTicks);
        super.render(graphics, mouseX, mouseY, deltaTicks);
        graphics.drawString(this.font, Component.translatable("qualityofqueso.gui.search_item_frames"), this.width / 2 - 135, this.height / 2 - 110, CommonColors.WHITE);
        graphics.drawString(this.font, Component.translatable("qualityofqueso.gui.search_item_frames.warning.line1"), this.width / 2 - 175, this.height / 2 - 90, CommonColors.WHITE);
        graphics.drawString(this.font, Component.translatable("qualityofqueso.gui.search_item_frames.warning.line2"), this.width / 2 - 110, this.height / 2 - 70, CommonColors.WHITE);
        graphics.drawString(this.font, Component.translatable("qualityofqueso.gui.search_item_frames.line3"), this.width / 2 - 155, this.height / 2 - 50, CommonColors.WHITE);
        this.searchButton.active = !this.searchField.getValue().isEmpty();
        if (!this.searchField.getValue().isEmpty() && this.searchButton.isMouseOver(mouseX, mouseY)) {
            drawTooltip(Component.translatable("qualityofqueso.gui.search.tooltip", this.searchField.getValue()), graphics, this.font, mouseX, mouseY);
        }
        if (this.clearButton.isMouseOver(mouseX, mouseY)) {
            drawTooltip(Component.translatable("qualityofqueso.gui.clear.tooltip"), graphics, this.font, mouseX, mouseY);
        }
        if (options().accessibility.tooltips.on() && this.searchField.isMouseOver(mouseX, mouseY) && this.searchField.getValue().isEmpty()) {
            drawTooltip(Component.translatable("qualityofqueso.gui.search_item_frames.search_filtering"), graphics, this.font, mouseX, mouseY);
        }
    }
}