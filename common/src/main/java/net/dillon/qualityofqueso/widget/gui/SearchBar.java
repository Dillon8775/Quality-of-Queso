package net.dillon.qualityofqueso.widget.gui;

import net.dillon.qualityofqueso.option.eum.accessibility.WidgetTheme;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.CommonColors;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CancellationException;

import static net.dillon.qualityofqueso.helper.ButtonHelper.getWidgetPath;
import static net.dillon.qualityofqueso.helper.GuiHelper.drawTooltip;
import static net.dillon.qualityofqueso.helper.ModHelper.*;
import static net.dillon.qualityofqueso.util.ModConstants.DEFAULT_TRANSPARENT_SEARCH_BAR_TEXT_COLOR;
import static net.dillon.qualityofqueso.util.ModConstants.SEARCH_TEXTURE;

/**
 * An representation of a search bar.
 */
public class SearchBar extends EditBox {
    private static final int OVERLAY_WIDTH = 96;
    private static final int OVERLAY_HEIGHT = 18;
    private final Font font;

    public SearchBar(Font font, int x, int y) {
        super(font, x, y, 90, 12, Component.empty());
        this.font = font;
        if (options().searching.saveSearchText) {
            this.setValue(ModConstants.SAVED_TEXT);
        }
        this.setMaxLength(50);
        if (options().accessibility.widgetTheme.searchBarTransparent() && !options().searching.searchBarColor.black()) {
            this.setTextShadow(false);
            int textColor = options().searching.searchBarTextColor;
            if (options().searching.searchBarTextColor == DEFAULT_TRANSPARENT_SEARCH_BAR_TEXT_COLOR
                    && (options().accessibility.widgetTheme != WidgetTheme.VANILLA || options().searching.searchBarColor.transparent())) {
                textColor = CommonColors.WHITE;
            }
            if (options().accessibility.widgetTheme == WidgetTheme.VANILLA && options().searching.searchBarColor.transparent()) {
                textColor = options().searching.searchBarTextColor;
            }
            this.setTextColor(textColor);
            this.addFormatter((text, offset) -> FormattedCharSequence.forward(text, Style.EMPTY.withUnderlined(options().searching.underlineText)));
            this.setCentered(true);
        } else {
            this.setHint(Component.translatable("qualityofqueso.gui.search.placeholder").withStyle(ChatFormatting.ITALIC));
        }
    }

    /**
     * @return new widget sprites for the search bar.
     */
    public static WidgetSprites getSprites() {
        return new WidgetSprites(
                ofQoQ("widget/search/" + getWidgetPath() + "search_bar"), ofQoQ("widget/search/" + getWidgetPath() + "search_bar_highlighted")
        );
    }

    /**
     * Cancels out closing the screen if the search bar was pressed.
     */
    public static void hasClickedOnBox(double mx, double my, EditBox editBox, CallbackInfoReturnable<Boolean> cir) throws CancellationException {
        if (!(editBox instanceof SearchBar searchBar)) {
            return;
        }

        if (searchBar.isMouseOver(mx, my)) {
            cir.setReturnValue(false);
        }
    }

    /**
     * Updates saved text in real time.
     */
    @Override
    public void insertText(String input) {
        super.insertText(input);
        ModConstants.SAVED_TEXT = this.getValue();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        if (this.isHovered()) {
            if (click.button() == 1) {
                this.setValue("");
                this.setFocused(false);
                return true;
            } else if (click.button() == 0) {
                this.setFocused(true);
                this.onClick(click, doubled);
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    /**
     * Checks if the mouse is over top of the panel.
     */
    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (!this.visible) {
            return false;
        }

        if (!options().searching.searchBarPosition.top() || options().searching.searchBarColor.black()) {
            return super.isMouseOver(mouseX, mouseY);
        }

        return mouseX >= (this.getX() - 4) && mouseX < (this.getX() + 4) + this.width && mouseY >= (this.getY() - 2) && mouseY < (this.getY() + 2) + this.height;
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        if (!options().searching.searchBarColor.black()) {
            if ((this.isFocused() || !options().searching.searchBarColor.transparent()) && options().searching.searchBarPosition.top()) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ofQoQ("widget/search/" + getWidgetPath() + "search_bar_overlay"),
                        this.getX() - 3, this.getY() - 3, OVERLAY_WIDTH, OVERLAY_HEIGHT);
            }
            if (!this.isFocused() && this.getValue().isEmpty() && options().accessibility.widgetTheme.searchBarTransparent()) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SEARCH_TEXTURE, this.getX() + (getCurrentScreen() instanceof InventoryScreen ? 78 : 80), this.getY(), 12, 12);
            }
        }
        super.extractWidgetRenderState(graphics, mouseX, mouseY, deltaTicks);
        if (options().accessibility.tooltips.on() && this.isHovered() && this.getValue().isEmpty()) {
            Component matchCase = Component.literal(":").withStyle(ChatFormatting.BOLD).withColor(0xC4FFD7);
            Component multiple = Component.literal(",").withStyle(ChatFormatting.ITALIC);
            Component tag = Component.literal("#").withStyle(ChatFormatting.ITALIC).withColor(0x7FFFFF);
            Component exclude = Component.literal("!").withStyle(ChatFormatting.BOLD).withColor(0xFF4242);
            drawTooltip(Component.translatable("qualityofqueso.gui.chest_search.search_filtering",
                    matchCase.copy(), multiple.copy(), tag.copy(), exclude.copy()
            ), graphics, this.font, mouseX, mouseY, true);
        }
    }
}