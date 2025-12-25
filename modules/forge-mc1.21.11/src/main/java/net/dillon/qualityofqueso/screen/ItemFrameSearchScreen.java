package net.dillon.qualityofqueso.screen;

import net.dillon.qualityofqueso.main.QoQ;
import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.option.ModListOptions;
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
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

/**
 * A utility screen to search for all nearby item frames. If an item frame is found, it glows.
 */
@OnlyIn(Dist.CLIENT)
public class ItemFrameSearchScreen extends Screen {
    private EditBox searchField;
    private Button searchButton, clearButton;

    // Basic constructor; no title text.
    public ItemFrameSearchScreen() {
        super(ModTexts.BLANK);
    }

    /**
     * Initializes the {@code search field} box and constructs the screen.
     */
    @Override
    protected void init() {
        this.searchField = new EditBox(this.font, this.width / 2 - 100, this.height / 2 - 24, 200, 20, Component.empty());
        if (ModClientOptions.SAVE_SEARCH_TEXT.get()) {
            this.searchField.setValue(QoQ.SAVED_ITEM_FRAME_TEXT);
        }
        this.searchField.setMaxLength(50);
        this.searchButton = this.addRenderableWidget(Button.builder(Component.translatable("qualityofqueso.gui.search"), button -> {
            this.sendPacket(false, ModClientOptions.ITEM_FRAME_SEARCH_TIMER.get() != 0 ? ModClientOptions.ITEM_FRAME_SEARCH_TIMER.get() : 0, ModClientOptions.ITEM_FRAME_SEARCH_RADIUS.get());
        }).bounds(this.width / 2 + 115, this.height / 2 + 24, 100, 20).build());
        AbstractWidget itemFrameSearchTimer = this.addRenderableWidget(ModListOptions.ITEM_FRAME_SEARCH_TIMER.createButton(Minecraft.getInstance().options));
        itemFrameSearchTimer.setRectangle(100, 20, this.width / 2 + 5, this.height / 2 + 24);
        AbstractWidget itemFrameSearchRadius = this.addRenderableWidget(ModListOptions.ITEM_FRAME_SEARCH_RADIUS.createButton(Minecraft.getInstance().options));
        itemFrameSearchRadius.setRectangle(100, 20, itemFrameSearchTimer.getX(), itemFrameSearchTimer.getY() + 32);
        this.clearButton = this.addRenderableWidget(Button.builder(Component.translatable("qualityofqueso.gui.clear"), button -> {
            this.searchField.setValue("");
            this.sendPacket(true, 0, ModClientOptions.ITEM_FRAME_SEARCH_RADIUS.get());
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
        this.searchField.render(graphics, mouseX, mouseY, deltaTicks);
        super.render(graphics, mouseX, mouseY, deltaTicks);
        graphics.drawString(this.font, Component.translatable("qualityofqueso.gui.search_item_frames"), this.width / 2 - 135, this.height / 2 - 104, CommonColors.WHITE);
        graphics.drawString(this.font, Component.translatable("qualityofqueso.gui.search_item_frames.warning.line1"), this.width / 2 - 175, this.height / 2 - 80, CommonColors.WHITE);
        graphics.drawString(this.font, Component.translatable("qualityofqueso.gui.search_item_frames.warning.line2"), this.width / 2 - 110, this.height / 2 - 56, CommonColors.WHITE);
        this.searchButton.active = !this.searchField.getValue().isEmpty();
        if (!this.searchField.getValue().isEmpty() && this.searchButton.isHovered()) {
            ButtonUtil.drawTooltip(!Minecraft.getInstance().hasControlDown() ?
                    Component.translatable("qualityofqueso.gui.search.tooltip", this.searchField.getValue()) :
                    Component.translatable("qualityofqueso.gui.search.match_case.tooltip", this.searchField.getValue()), graphics, this.font, mouseX, mouseY);
        }
        if (this.clearButton.isHovered()) {
            ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.clear.tooltip"), graphics, this.font, mouseX, mouseY);
        }
        if (ModClientOptions.HELPFUL_TOOLTIPS.get() && this.searchField.isHovered() && this.searchField.getValue().isEmpty()) {
            ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.search_item_frames.search_filtering"), graphics, this.font, mouseX, mouseY);
        }
        this.renderBlurredBackground(graphics);
    }

    /**
     * Similar to super {@link Screen#render(GuiGraphics, int, int, float)}, we just aren't rendering the blur.
     */
    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        if (this.minecraft.level == null) {
            this.renderPanorama(graphics, deltaTicks);
        }

        this.renderMenuBackground(graphics);
    }

    /**
     * Handles key pressing.
     */
    @Override
    public boolean keyPressed(KeyEvent input) {
        // Send the packet upon pressing enter.
        if (input.key() == GLFW.GLFW_KEY_ENTER && !this.searchField.getValue().isEmpty()) {
            this.sendPacket(false, ModClientOptions.ITEM_FRAME_SEARCH_TIMER.get() != 0 ? ModClientOptions.ITEM_FRAME_SEARCH_TIMER.get() : 0, ModClientOptions.ITEM_FRAME_SEARCH_RADIUS.get());
        }
        return super.keyPressed(input);
    }

    /**
     * Saves the text in {@code search field} when closing the screen.
     */
    @Override
    public void onClose() {
        QoQ.SAVED_ITEM_FRAME_TEXT = this.searchField.getValue();
        QoQ.saveAll();
        super.onClose();
    }

    /**
     * Closes the screen and sends the glowing packet.
     */
    private void sendPacket(boolean clear, int timer, int radius) {
        this.onClose();
        String text = this.searchField.getValue();
        boolean matchCase = text.startsWith(":");
        ServerHandler.sendToServer(new GlowSearchC2SPayload(text.substring(matchCase ? 1 : 0), matchCase, clear, timer, radius));
    }
}