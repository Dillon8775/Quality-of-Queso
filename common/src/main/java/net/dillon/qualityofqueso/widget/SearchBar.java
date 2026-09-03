package net.dillon.qualityofqueso.widget;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.qualityofqueso.option.eum.general.Theme;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.CommonColors;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CancellationException;

import static net.dillon.dillonlib.task.ClientTasks.drawSprite;
import static net.dillon.dillonlib.task.ClientTasks.getScreen;
import static net.dillon.qualityofqueso.helper.ButtonHelper.getWidgetPath;
import static net.dillon.qualityofqueso.helper.GuiHelper.drawTooltip;
import static net.dillon.qualityofqueso.helper.ModConstants.DEFAULT_TRANSPARENT_SEARCH_BAR_TEXT_COLOR;
import static net.dillon.qualityofqueso.helper.ModConstants.SEARCH_TEXTURE;
import static net.dillon.qualityofqueso.helper.ModHelper.qoqIdentifier;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.updateClient;

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
        if (client().searching().saveSearchText) {
            this.setValue(client().searching().savedSearchText);
        }
        this.setMaxLength(50);
        if (client().general().theme.searchBarTransparent() && !client().searching().searchBarColor.black()) {
            this.setTextShadow(false);
            int textColor = client().searching().searchBarTextColor;
            if (client().searching().searchBarTextColor == DEFAULT_TRANSPARENT_SEARCH_BAR_TEXT_COLOR
                    && (client().general().theme != Theme.VANILLA || client().searching().searchBarColor.transparent())) {
                textColor = CommonColors.WHITE;
            }
            if (client().general().theme == Theme.VANILLA && client().searching().searchBarColor.transparent()) {
                textColor = client().searching().searchBarTextColor;
            }
            this.setTextColor(textColor);
            this.addFormatter((text, offset) -> FormattedCharSequence.forward(text, Style.EMPTY.withUnderlined(client().searching().underlineText)));
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
                qoqIdentifier("widget/search/" + getWidgetPath(true) + "search_bar"), qoqIdentifier("widget/search/" + getWidgetPath(true) + "search_bar_highlighted")
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
        updateClient(client -> client.searching().savedSearchText = this.getValue());
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        if (this.isHovered()) {
            if (click.button() == InputConstants.MOUSE_BUTTON_RIGHT) {
                this.setValue("");
                this.setFocused(false);
                return true;
            } else if (click.button() == InputConstants.MOUSE_BUTTON_LEFT) {
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

        if (!client().searching().searchBarPosition.top() || client().searching().searchBarColor.black()) {
            return super.isMouseOver(mouseX, mouseY);
        }

        return mouseX >= (this.getX() - 4) && mouseX < (this.getX() + 4) + this.width && mouseY >= (this.getY() - 2) && mouseY < (this.getY() + 2) + this.height;
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        if (!client().searching().searchBarColor.black()) {
            if ((this.isFocused() || !client().searching().searchBarColor.transparent()) && client().searching().searchBarPosition.top()) {
                drawSprite(
                        graphics,
                        qoqIdentifier("widget/search/" + getWidgetPath(true) + "search_bar_overlay"),
                        this.getX() - 3,
                        this.getY() - 3,
                        OVERLAY_WIDTH,
                        OVERLAY_HEIGHT
                );
            }
            if (!this.isFocused() && this.getValue().isEmpty() && client().general().theme.searchBarTransparent()) {
                drawSprite(
                        graphics,
                        SEARCH_TEXTURE,
                        this.getX() + (getScreen() instanceof InventoryScreen ? 78 : 80),
                        this.getY(),
                        12,
                        12
                );
            }
        }
        super.extractWidgetRenderState(graphics, mouseX, mouseY, deltaTicks);
        if (client().general().tooltips.enabled() && this.isHovered() && this.getValue().isEmpty()) {
            Component matchCase = Component.literal(":").withStyle(ChatFormatting.BOLD).withColor(0xC4FFD7);
            Component multiple = Component.literal(",").withStyle(ChatFormatting.ITALIC);
            Component tag = Component.literal("#").withStyle(ChatFormatting.ITALIC).withColor(0x7FFFFF);
            Component exclude = Component.literal("!").withStyle(ChatFormatting.BOLD).withColor(0xFF4242);
            Component mod = Component.literal("@").withStyle(ChatFormatting.ITALIC);
            drawTooltip(Component.translatable("qualityofqueso.gui.chest_search.search_filtering",
                    matchCase.copy(), multiple.copy(), tag.copy(), exclude.copy(), mod.copy()
            ), graphics, this.font, mouseX, mouseY, true);
        }
    }
}