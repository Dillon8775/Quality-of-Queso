package net.dillon.qualityofqueso.screen;

import net.dillon.qualityofqueso.main.QoQ;
import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.option.ModListOptions;
import net.dillon.qualityofqueso.packet.GlowSearchC2SPayload;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.dillon.qualityofqueso.util.ModTexts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import static net.dillon.qualityofqueso.main.QoQ.options;

/**
 * A utility screen to search for all nearby item frames. If an item frame is found, it glows.
 */
@Environment(EnvType.CLIENT)
public class ItemFrameSearchScreen extends Screen {
    private TextFieldWidget searchField;
    private ButtonWidget searchButton, clearButton;

    // Basic constructor; no title text.
    public ItemFrameSearchScreen() {
        super(ModTexts.BLANK);
    }

    /**
     * Initializes the {@code search field} box and constructs the screen.
     */
    @Override
    protected void init() {
        this.searchField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, this.height / 2 - 24, 200, 20, null);
        if (options().saveSearchText) {
            this.searchField.setText(QoQ.SAVED_ITEM_FRAME_TEXT);
        }
        this.searchField.setMaxLength(50);
        this.searchButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("qualityofqueso.gui.search"), button -> {
            this.sendPacket(false, options().itemFrameSearchTimer != 0 ? options().itemFrameSearchTimer : 0, options().itemFrameSearchRadius);
        }).dimensions(this.width / 2 + 115, this.height / 2 + 24, 100, 20).build());
        ClickableWidget itemFrameSearchTimer = this.addDrawableChild(ModListOptions.ITEM_FRAME_SEARCH_TIMER.createWidget(MinecraftClient.getInstance().options));
        itemFrameSearchTimer.setDimensionsAndPosition(100, 20, this.width / 2 + 5, this.height / 2 + 24);
        ClickableWidget itemFrameSearchRadius = this.addDrawableChild(ModListOptions.ITEM_FRAME_SEARCH_RADIUS.createWidget(MinecraftClient.getInstance().options));
        itemFrameSearchRadius.setDimensionsAndPosition(100, 20, itemFrameSearchTimer.getX(), itemFrameSearchTimer.getY() + 32);
        this.clearButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("qualityofqueso.gui.clear"), button -> {
            this.searchField.setText("");
            this.sendPacket(true, 0, options().itemFrameSearchRadius);
        }).dimensions(this.width / 2 - 105, this.height / 2 + 24, 100, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("qualityofqueso.gui.close"), button -> {
            this.close();
        }).dimensions(this.width / 2 - 215, this.height / 2 + 24, 100, 20).build());
        this.addSelectableChild(this.searchField);
        this.setInitialFocus(this.searchField);
    }

    /**
     * Basic rendering; render the {@code search field}, other texts and tooltips, and basic background.
     */
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.searchField.render(context, mouseX, mouseY, deltaTicks);
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawTextWithShadow(this.textRenderer, Text.translatable("qualityofqueso.gui.search_item_frames"), this.width / 2 - 135, this.height / 2 - 104, -2039584);
        context.drawTextWithShadow(this.textRenderer, Text.translatable("qualityofqueso.gui.search_item_frames.warning.line1"), this.width / 2 - 175, this.height / 2 - 80, -2039584);
        context.drawTextWithShadow(this.textRenderer, Text.translatable("qualityofqueso.gui.search_item_frames.warning.line2"), this.width / 2 - 110, this.height / 2 - 56, -2039584);
        this.searchButton.active = !this.searchField.getText().isEmpty();
        if (!this.searchField.getText().isEmpty() && this.searchButton.isHovered()) {
            ButtonUtil.drawTooltip(!MinecraftClient.getInstance().isCtrlPressed() ?
                            Text.translatable("qualityofqueso.gui.search.tooltip", this.searchField.getText()) :
                            Text.translatable("qualityofqueso.gui.search.match_case.tooltip", this.searchField.getText()),
                    context, this.textRenderer, mouseX, mouseY);
        }
        if (this.clearButton.isHovered()) {
            ButtonUtil.drawTooltip(Text.translatable("qualityofqueso.gui.clear.tooltip"), context, this.textRenderer, mouseX, mouseY);
        }
        if (options().helpfulTooltips && this.searchField.isHovered() && this.searchField.getText().isEmpty()) {
            ButtonUtil.drawTooltip(Text.translatable("qualityofqueso.gui.search_item_frames.search_filtering"), context, this.textRenderer, mouseX, mouseY);
        }
        this.applyBlur(context);
    }

    /**
     * Similar to super {@link Screen#render(DrawContext, int, int, float)}, we just aren't rendering the blur.
     */
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (this.client.world == null) {
            this.renderPanoramaBackground(context, deltaTicks);
        }

        this.renderDarkening(context);
    }

    /**
     * Handles key pressing.
     */
    @Override
    public boolean keyPressed(KeyInput input) {
        // Send packet upon pressing enter.
        if (input.key() == GLFW.GLFW_KEY_ENTER && !this.searchField.getText().isEmpty()) {
            this.sendPacket(false, options().itemFrameSearchTimer != 0 ? options().itemFrameSearchTimer : 0, options().itemFrameSearchRadius);
        }
        return super.keyPressed(input);
    }

    /**
     * Saves the text in {@code search field} when closing the screen.
     */
    @Override
    public void close() {
        QoQ.SAVED_ITEM_FRAME_TEXT = this.searchField.getText();
        ModClientOptions.CLIENT_OPTIONS.save();
        super.close();
    }

    /**
     * Closes the screen and sends the glowing packet.
     */
    private void sendPacket(boolean clear, int timer, int radius) {
        this.close();
        ClientPlayNetworking.send(new GlowSearchC2SPayload(this.searchField.getText(), MinecraftClient.getInstance().isCtrlPressed(), clear, timer, radius));
    }
}