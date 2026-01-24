package net.dillon.qualityofqueso.screen;

import net.dillon.qualityofqueso.main.QoQ;
import net.dillon.qualityofqueso.option.ModListOptions;
import net.dillon.qualityofqueso.option.instance.ModClientOptions;
import net.dillon.qualityofqueso.packet.GlowSearchC2SPayload;
import net.dillon.qualityofqueso.packet.ServerHandler;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.dillon.qualityofqueso.util.ModTexts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

import static net.dillon.qualityofqueso.main.QoQ.options;

/**
 * A utility screen to search for all nearby item frames. If an item frame is found, it glows.
 */
@OnlyIn(Dist.CLIENT)
public class ItemFrameSearchScreen extends Screen {
    private EditBox searchField;
    private Button searchButton, clearButton;
    private final Screen parent;
    private boolean setParent = true;

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
        if (options().saveSearchText) {
            this.searchField.setValue(QoQ.SAVED_ITEM_FRAME_TEXT);
        }
        this.searchField.setMaxLength(50);
        this.searchButton = this.addRenderableWidget(Button.builder(Component.translatable("qualityofqueso.gui.search"), button -> {
            this.sendPacket(false, options().itemFrameSearchGlowDuration != 0 ? options().itemFrameSearchGlowDuration : 0, options().itemFrameSearchRadius);
        }).bounds(this.width / 2 + 115, this.height / 2 + 24, 100, 20).build());
        AbstractWidget itemFrameSearchRadius = this.addRenderableWidget(ModListOptions.itemFrameSearchRadius().createButton(Minecraft.getInstance().options, this.width / 2 + 5, 20, 100));
        itemFrameSearchRadius.setY(this.height / 2 + 24);
        AbstractWidget itemFrameSearchGlowDuration = this.addRenderableWidget(ModListOptions.itemFrameSearchGlowDuration().createButton(Minecraft.getInstance().options, this.width / 2 - 75, 20, 150));
        itemFrameSearchGlowDuration.setY(itemFrameSearchRadius.getY() + 32);
        this.clearButton = this.addRenderableWidget(Button.builder(Component.translatable("qualityofqueso.gui.clear"), button -> {
            this.clear();
        }).bounds(this.width / 2 - 105, this.height / 2 + 24, 100, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("qualityofqueso.gui.close"), button -> {
            this.onClose();
        }).bounds(this.width / 2 - 215, this.height / 2 + 24, 100, 20).build());
        this.addWidget(this.searchField);
        this.setInitialFocus(this.searchField);
    }

     /**
     * Basic rendering; render the {@code search field}, other texts and tooltips, and basic background.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        super.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, deltaTicks);
        this.searchField.render(graphics, mouseX, mouseY, deltaTicks);
        graphics.drawString(this.font, Component.translatable("qualityofqueso.gui.search_item_frames"), this.width / 2 - 135, this.height / 2 - 110, CommonColors.WHITE);
        graphics.drawString(this.font, Component.translatable("qualityofqueso.gui.search_item_frames.warning.line1"), this.width / 2 - 175, this.height / 2 - 90, CommonColors.WHITE);
        graphics.drawString(this.font, Component.translatable("qualityofqueso.gui.search_item_frames.warning.line2"), this.width / 2 - 110, this.height / 2 - 70, CommonColors.WHITE);
        graphics.drawString(this.font, Component.translatable("qualityofqueso.gui.search_item_frames.line3"), this.width / 2 - 155, this.height / 2 - 50, CommonColors.WHITE);
        this.searchButton.active = !this.searchField.getValue().isEmpty();
        if (!this.searchField.getValue().isEmpty() && this.searchButton.isHovered()) {
            ButtonUtil.drawTooltip(!Screen.hasControlDown() ?
                    Component.translatable("qualityofqueso.gui.search.tooltip", this.searchField.getValue()) :
                    Component.translatable("qualityofqueso.gui.search.match_case.tooltip", this.searchField.getValue()), graphics, this.font, mouseX, mouseY);
        }
        if (this.clearButton.isHovered()) {
            ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.clear.tooltip"), graphics, this.font, mouseX, mouseY);
        }
        if (options().helpfulTooltips && this.searchField.isHovered() && this.searchField.getValue().isEmpty()) {
            ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.search_item_frames.search_filtering"), graphics, this.font, mouseX, mouseY);
        }
    }

    /**
     * Handles key pressing.
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Send the packet upon pressing enter.
        if (keyCode == GLFW.GLFW_KEY_ENTER && !this.searchField.getValue().isEmpty()) {
            this.sendPacket(false, options().itemFrameSearchGlowDuration != 0 ? options().itemFrameSearchGlowDuration : 0, options().itemFrameSearchRadius);
        }
        if (Screen.hasControlDown() && keyCode == GLFW.GLFW_KEY_C) {
            this.clear();
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * Saves the text in {@code search field} when closing the screen.
     */
    @Override
    public void onClose() {
        QoQ.SAVED_ITEM_FRAME_TEXT = this.searchField.getValue();
        ModClientOptions.CLIENT.save();
        if (this.parent != null && this.setParent) {
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
        this.sendPacket(true, 0, options().itemFrameSearchRadius);
    }

    /**
     * Closes the screen and sends the glowing packet.
     */
    private void sendPacket(boolean clear, int timer, int radius) {
        this.setParent = false;
        this.onClose();
        String text = this.searchField.getValue();
        boolean colon = text.startsWith(":");
        boolean matchCase = colon || Screen.hasControlDown();
        boolean shouldSubstring = (Screen.hasControlDown() && colon) || colon;
        ServerHandler.sendToServer(new GlowSearchC2SPayload(text.substring(shouldSubstring ? 1 : 0), matchCase, clear, timer, radius));
    }
}