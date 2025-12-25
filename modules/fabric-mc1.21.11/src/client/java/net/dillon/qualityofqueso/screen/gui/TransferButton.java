package net.dillon.qualityofqueso.screen.gui;

import net.dillon.qualityofqueso.util.ButtonUtil;
import net.dillon.qualityofqueso.util.ModTexts;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Supplier;

import static net.dillon.qualityofqueso.main.QoQ.options;
import static net.minecraft.text.Text.translatable;

/**
 * A representation of a transfer button.
 */
public class TransferButton extends ButtonWidget {
    private final ScreenHandler screenHandler;
    protected final String searchFieldText;
    protected final TextRenderer textRenderer;
    private final String buttonName;
    private final Supplier<Boolean> canBeActive;

    /**
     * Constructs a default transfer button.
     */
    public TransferButton(ScreenHandler screenHandler, TextRenderer textRenderer, String searchFieldText, int x, int y, String buttonName, PressAction onPress) {
        super(x, y, 10, 10, ModTexts.BLANK, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.screenHandler = screenHandler;
        this.textRenderer = textRenderer;
        this.searchFieldText = searchFieldText;
        this.buttonName = buttonName;
        this.canBeActive = () -> this.active;
    }

    /**
     * Constructs a default transfer button with a boolean supplier, determining if the button can be active or not.
     */
    public TransferButton(ScreenHandler screenHandler, TextRenderer textRenderer, String searchFieldText, int x, int y, String buttonName, PressAction onPress, Supplier<Boolean> canBeActive) {
        super(x, y, 10, 10, ModTexts.BLANK, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.buttonName = buttonName;
        this.screenHandler = screenHandler;
        this.textRenderer = textRenderer;
        this.searchFieldText = searchFieldText;
        this.canBeActive = canBeActive;
    }

    /**
     * Fully blocks sound and click if button shouldn't be activated.
     */
    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (!this.canBeActive.get()) {
            return false;
        }
        return super.mouseClicked(click, doubled);
    }

    /**
     * Renders a transfer button texture.
     */
    @Unique
    protected void renderButtonTexture(String id, boolean transferable, ClickableWidget buttonReference, DrawContext
            context) {
        String transferableString = !this.screenHandler.getCursorStack().isEmpty() ?
                "_with_stack.png" : this.searchFieldText.startsWith("!") ?
                "_exclude.png" : this.searchFieldText.startsWith("#") ?
                "_with_tag.png" : this.searchFieldText.startsWith(":") ?
                "_match.png" : ".png";
        String appended = transferable ? transferableString : ".png";
        context.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of("qualityofqueso:textures/gui/" + id + appended), buttonReference.getX() - 1, buttonReference.getY() - 1, 0.0F, 0.0F, 12, 12, 12, 12);
    }

    /**
     * Renders the textures and tooltips for the button.
     */
    @Override
    protected void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.active = this.canBeActive.get();

        if (this.canBeActive.get()) {
            this.renderButtonTexture(this.isHovered() ?
                    this.buttonName + "_button_hovered" :
                    this.buttonName + "_button", true, this, context);
        } else {
            this.renderButtonTexture(this.buttonName + "_button_inactive", true, this, context);
        }

        if (this.isHovered()) {
            if (this.active) {
                if (!this.screenHandler.getCursorStack().isEmpty()) {
                    ButtonUtil.drawTooltip(translatable("qualityofqueso.gui." + this.buttonName + "_button.with_cursor_stack", this.screenHandler.getCursorStack().getItemName()), context, this.textRenderer, mouseX, mouseY);
                } else if (!this.searchFieldText.isEmpty()) {
                    ButtonUtil.drawTooltip(this.searchFieldText.startsWith("#") ?
                            translatable("qualityofqueso.gui." + this.buttonName + "_button.with_search_query.tag", this.searchFieldText.substring(1)) :
                            this.searchFieldText.startsWith("!") ?
                                    translatable("qualityofqueso.gui." + this.buttonName + "_button.with_search_query.exclude", this.searchFieldText.substring(1)) :
                                    this.searchFieldText.startsWith(":") ?
                                            translatable("qualityofqueso.gui." + this.buttonName + "_button.with_search_query.match", this.searchFieldText.substring(1)) :
                                            translatable("qualityofqueso.gui." + this.buttonName + "_button.with_search_query", this.searchFieldText), context, this.textRenderer, mouseX, mouseY);
                } else {
                    if (options().helpfulTooltips) {
                        ButtonUtil.drawTooltip(translatable("qualityofqueso.gui." + this.buttonName + "_button"), context, this.textRenderer, mouseX, mouseY);
                    }
                }
            }
        }
    }
}