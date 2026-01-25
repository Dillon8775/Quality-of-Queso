package net.dillon.qualityofqueso.screen.gui;

import net.dillon.qualityofqueso.main.QoQ;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.dillon.qualityofqueso.main.QoQ.options;

/**
 * An representation of a search field.
 */
@Environment(EnvType.CLIENT)
public class SearchField extends TextFieldWidget {
    private final TextRenderer textRenderer;

    public SearchField(TextRenderer textRenderer, int x, int y) {
        super(textRenderer, x, y, 90, 12, null);
        this.textRenderer = textRenderer;
        if (options().saveSearchText) {
            this.setText(QoQ.SAVED_TEXT);
        }
        this.setMaxLength(50);
        this.setPlaceholder(Text.translatable("qualityofqueso.gui.search.placeholder").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.renderWidget(context, mouseX, mouseY, deltaTicks);
        if (options().helpfulTooltips && this.isHovered() && this.getText().isEmpty()) {
            ButtonUtil.drawTooltip(Text.translatable("qualityofqueso.gui.chest_search.search_filtering"), context, this.textRenderer, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (this.isHovered() && click.button() == 1) {
            this.setText("");
            this.setFocused(false);
            return true;
        }
        return super.mouseClicked(click, doubled);
    }
}