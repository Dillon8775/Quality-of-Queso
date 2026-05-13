package net.dillon.qualityofqueso.widget.gui;

import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CancellationException;

import static net.dillon.qualityofqueso.helper.ButtonHelper.getWidgetPath;
import static net.dillon.qualityofqueso.helper.GuiHelper.drawTooltip;
import static net.dillon.qualityofqueso.helper.ModHelper.ofQoQ;
import static net.dillon.qualityofqueso.helper.ModHelper.options;

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
        this.setHint(Component.translatable("qualityofqueso.gui.search.placeholder").withStyle(ChatFormatting.ITALIC));
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

        if (!options().searching.searchBarPosition.top() || options().searching.searchBarColor.black()) {
            return super.isMouseOver(mouseX, mouseY);
        }

        return mouseX >= (this.getX() - 4) && mouseX < (this.getX() + 4) + this.width && mouseY >= (this.getY() - 2) && mouseY < (this.getY() + 2) + this.height;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        if (!options().searching.searchBarColor.black() && options().searching.searchBarPosition.top()) {
            graphics.blit(ofQoQ("textures/gui/sprites/widget/search/" + getWidgetPath() + "search_bar_overlay.png"),
                    this.getX() - 3,
                    this.getY() - 3,
                    0.0F,
                    0.0F,
                    OVERLAY_WIDTH, OVERLAY_HEIGHT,
                    OVERLAY_WIDTH, OVERLAY_HEIGHT);
        }
        super.renderWidget(graphics, mouseX, mouseY, deltaTicks);
        if (options().accessibility.tooltips.on() && this.isMouseOver(mouseX, mouseY) && this.getValue().isEmpty()) {
            Component matchCase = Component.literal(":").withStyle(ChatFormatting.BOLD);
            Component multiple = Component.literal(",").withStyle(ChatFormatting.ITALIC);
            Component tag = Component.literal("#").withStyle(ChatFormatting.ITALIC);
            Component exclude = Component.literal("!").withStyle(ChatFormatting.BOLD);
            drawTooltip(Component.translatable("qualityofqueso.gui.chest_search.search_filtering",
                    matchCase.copy(), multiple.copy(), tag.copy(), exclude.copy()
            ), graphics, this.font, mouseX, mouseY, true);
        }
    }
}