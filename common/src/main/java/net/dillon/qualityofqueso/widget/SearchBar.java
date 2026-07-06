package net.dillon.qualityofqueso.widget;

import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.option.eum.general.Theme;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CancellationException;

import static net.dillon.qualityofqueso.helper.ButtonHelper.getWidgetPath;
import static net.dillon.qualityofqueso.helper.GuiHelper.drawTooltip;
import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;
import static net.dillon.qualityofqueso.helper.ModHelper.ofQoQ;

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
        if (clientOptionsInstance().getSearchingOptions().saveSearchText) {
            this.setValue(clientOptionsInstance().getSearchingOptions().savedSearchText);
        }
        this.setMaxLength(50);
        this.setHint(Component.translatable("qualityofqueso.gui.search.placeholder").withStyle(ChatFormatting.ITALIC));
    }

    /**
     * @return new widget sprites for the search bar.
     */
    public static WidgetSprites getSprites() {
        String path = getWidgetPath(true);
        if (clientOptionsInstance().getGeneralOptions().theme == Theme.TRANSPARENT) {
            path = !clientOptionsInstance().getSearchingOptions().searchBarColor.black() ? "vanilla/" : getWidgetPath(true);
        }
        return new WidgetSprites(
                ofQoQ("widget/search/" + path + "search_bar"), ofQoQ("widget/search/" + path + "search_bar_highlighted")
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
        ModClientOptions.INSTANCE.update(options -> options.getSearchingOptions().savedSearchText = this.getValue());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int bl) {
        if (this.isMouseOver(mouseX, mouseY)) {
            if (bl == 1) {
                this.setValue("");
                this.setFocused(false);
                return true;
            } else if (bl == 0) {
                this.setFocused(true);
                this.onClick(mouseX, mouseY);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, bl);
    }

    /**
     * Checks if the mouse is over top of the panel.
     */
    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (!this.visible) {
            return false;
        }

        if (!clientOptionsInstance().getSearchingOptions().searchBarPosition.top() || clientOptionsInstance().getSearchingOptions().searchBarColor.black()) {
            return super.isMouseOver(mouseX, mouseY);
        }

        return mouseX >= (this.getX() - 4) && mouseX < (this.getX() + 4) + this.width && mouseY >= (this.getY() - 2) && mouseY < (this.getY() + 2) + this.height;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        if (!clientOptionsInstance().getSearchingOptions().searchBarColor.black() && clientOptionsInstance().getSearchingOptions().searchBarPosition.top()) {
            graphics.blitSprite(ofQoQ("widget/search/" + getWidgetPath(true) + "search_bar_overlay"),
                    this.getX() - 3, this.getY() - 3, OVERLAY_WIDTH, OVERLAY_HEIGHT);
        }
        super.renderWidget(graphics, mouseX, mouseY, deltaTicks);
        if (clientOptionsInstance().getGeneralOptions().tooltips.enabled() && this.isMouseOver(mouseX, mouseY) && this.getValue().isEmpty()) {
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