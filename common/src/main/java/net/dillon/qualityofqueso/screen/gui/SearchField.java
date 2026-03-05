package net.dillon.qualityofqueso.screen.gui;

import net.dillon.qualityofqueso.util.ButtonUtil;
import net.dillon.qualityofqueso.util.ModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.util.ModUtil.options;

/**
 * An representation of a search field.
 */
public class SearchField extends EditBox {
    private final Font font;

    public SearchField(Font font, int x, int y) {
        super(font, x, y, 90, 12, Component.empty());
        this.font = font;
        if (options().saveSearchText) {
            this.setValue(ModUtil.SAVED_TEXT);
        }
        this.setMaxLength(50);
        this.setHint(Component.translatable("qualityofqueso.gui.search.placeholder").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        super.renderWidget(context, mouseX, mouseY, deltaTicks);
        if (options().helpfulTooltips && this.isHovered() && this.getValue().isEmpty()) {
            ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.chest_search.search_filtering"), context, this.font, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        if (this.isHovered() && click.button() == 1) {
            this.setValue("");
            this.setFocused(false);
            return true;
        }
        return super.mouseClicked(click, doubled);
    }
}
