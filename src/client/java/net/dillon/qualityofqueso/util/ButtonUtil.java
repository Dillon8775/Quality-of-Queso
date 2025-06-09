package net.dillon.qualityofqueso.util;

import net.dillon.qualityofqueso.option.ModOptionsScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Utility class.
 */
@Environment(EnvType.CLIENT)
public class ButtonUtil {

    /**
     * Initializes the settings button.
     */
    public static ButtonWidget initializeButton(MinecraftClient client, Screen parent, int width, int height) {
        return new ButtonWidget.Builder(ModTexts.BLANK, button -> {
            client.setScreen(new ModOptionsScreen(parent));
        }).dimensions(width, height, 20, 20).build();
    }

    /**
     * Draws the tooltip and texture for the settings button.
     */
    public static void drawTooltipAndTexture(DrawContext context, TextRenderer renderer, ButtonWidget button, int mouseX, int mouseY) {
        if (button.isHovered()) {
            drawTooltip(context, renderer, mouseX, mouseY);
        }
        drawTexture(context, button);
    }

    /**
     * Draws the tooltip over the settings button.
     */
    private static void drawTooltip(DrawContext context, TextRenderer renderer, int mouseX, int mouseY) {
        context.drawOrderedTooltip(renderer, renderer.wrapLines(Text.translatable("qualityofqueso.gui.options.tooltip"), 200), mouseX, mouseY);
    }

    /**
     * Draws the settings texture over top of the settings button.
     */
    private static void drawTexture(DrawContext context, ButtonWidget button) {
        context.drawTexture(RenderLayer::getGuiTextured, Identifier.of("qualityofqueso:textures/gui/cheesewheel.png"), button.getX() + 1, button.getY() + 1, 0.0F, 0.0F, 18, 18, 18, 18);
    }
}