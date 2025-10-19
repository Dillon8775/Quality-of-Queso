package net.dillon.qualityofqueso.util;

import net.dillon.qualityofqueso.option.ModOptionsScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

import static net.dillon.qualityofqueso.main.QoQ.options;

/**
 * Utility class.
 */
@Environment(EnvType.CLIENT)
public class ButtonUtil {
    public static final String CHEESE_WHEEL = "cheese_wheel";
    public static final String ENABLED_TEXTURE = "qoq_enabled";
    public static final String DISABLED_TEXTURE = "qoq_disabled";

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
    public static void drawTooltipAndTexture(Text tooltip, String name, DrawContext context, TextRenderer renderer, ButtonWidget button, int mouseX, int mouseY, @Nullable Float f) {
        if (options().helpfulTooltips && button.isHovered()) {
            drawTooltip(tooltip, context, renderer, mouseX, mouseY);
        }
        drawTexture(context, name, button, f == null ? 1.0F : f);
    }

    /**
     * Draws a tooltip.
     */
    public static void drawTooltip(Text tooltip, DrawContext context, TextRenderer renderer, int mouseX, int mouseY) {
        context.drawOrderedTooltip(renderer, renderer.wrapLines(tooltip, 200), mouseX, mouseY);
    }

    /**
     * Draws a texture over a button.
     */
    public static void drawTexture(DrawContext context, String name, ButtonWidget button, float f) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of("qualityofqueso:textures/gui/" + name + ".png"), button.getX() + 1, button.getY() + 1, 0.0F, 0.0F, 18, 18, 18, 18, ColorHelper.withAlpha(f, Colors.WHITE));
    }

    /**
     * Draws a texture over a button without a custom fade.
     */
    public static void drawTexture(DrawContext context, String name, ButtonWidget button) {
        drawTexture(context, name, button, 1.0F);
    }
}