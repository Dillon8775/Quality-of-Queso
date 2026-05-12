package net.dillon.qualityofqueso.screen.gui;

import net.dillon.qualityofqueso.util.ButtonUtil;
import net.dillon.qualityofqueso.util.ModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import static net.dillon.qualityofqueso.util.ModUtil.SAVED_TEXT;
import static net.dillon.qualityofqueso.util.ModUtil.options;

/**
 * An representation of a search field.
 */
public class SearchField extends EditBox {
    private final Font font;

    public SearchField(Font font, int x, int y) {
        super(font, x, y, 90, 12, Component.empty());
        this.font = font;
        if (options().searching.saveSearchText) {
            this.setValue(ModUtil.SAVED_TEXT);
        }
        this.setMaxLength(50);
        this.setHint(Component.translatable("qualityofqueso.gui.search.placeholder").withStyle(ChatFormatting.ITALIC));
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        super.renderWidget(context, mouseX, mouseY, deltaTicks);
        if (options().accessibility.helpfulTooltips && this.isMouseOver(mouseX, mouseY) && this.getValue().isEmpty()) {
            Component matchCase = Component.literal(":").withStyle(ChatFormatting.BOLD).withColor(0xC4FFD7);
            Component multiple = Component.literal(",").withStyle(ChatFormatting.ITALIC);
            Component tag = Component.literal("#").withStyle(ChatFormatting.ITALIC).withColor(0x7FFFFF);
            Component exclude = Component.literal("!").withStyle(ChatFormatting.BOLD).withColor(0xFF4242);
            ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.chest_search.search_filtering",
                    matchCase.copy(), multiple.copy(), tag.copy(), exclude.copy()
            ), context, this.font, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isMouseOver(mouseX, mouseY)) {
            if (button == 1) {
                this.setValue("");
                this.setFocused(false);
                return true;
            } else if (button == 0) {
                this.setFocused(true);
                this.onClick(mouseX, mouseY);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * Updates saved text in real time.
     */
    @Override
    public void insertText(String input) {
        super.insertText(input);
        SAVED_TEXT = this.getValue();
    }
}