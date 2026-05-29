package net.dillon.qualityofqueso.helper;

import net.dillon.qualityofqueso.platform.MultiLoader;
import net.dillon.qualityofqueso.screen.AbstractModScreen;
import net.dillon.qualityofqueso.util.ModTexts;
import net.dillon.qualityofqueso.widget.QuesoButton;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;

import static net.dillon.qualityofqueso.helper.ManagementHelper.getTransferButtonXY;
import static net.dillon.qualityofqueso.helper.ModHelper.*;
import static net.dillon.qualityofqueso.util.ModConstants.CHEESE_WHEEL_TEXTURE;
import static net.dillon.qualityofqueso.util.ModConstants.YOUTUBE_TEXTURE;

/**
 * Utility and helper class for buttons.
 */
public class ButtonHelper {

    /**
     * Draws a texture over a button without a custom fade.
     */
    public static void drawTexture(GuiGraphicsExtractor graphics, String name, Button button) {
        drawTexture(graphics, name, button, 1.0F);
    }

    /**
     * Draws the texture for a {@code inventory management button.}
     */
    public static void drawButtonTexture(GuiGraphicsExtractor graphics, String name, QuesoButton button) {
        int xy = getTransferButtonXY(button);
        graphics.blit(RenderPipelines.GUI_TEXTURED, ofQoQ("textures/gui/sprites/button/" + name + ".png"), button.getX() - 1, button.getY() - 1, 0.0F, 0.0F, xy, xy, xy, xy);
    }

    /**
     * Draws a texture over a button.
     */
    public static void drawTexture(GuiGraphicsExtractor graphics, String name, Button button, float f) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, ofQoQ("textures/gui/" + name + ".png"), button.getX() + 2, button.getY() + 2, 0.0F, 0.0F, 16, 16, 16, 16, ARGB.color(f, CommonColors.WHITE));
    }

    /**
     * Creates the Quality of Queso {@code menu button.}
     */
    public static SpriteIconButton createMenuButton(int x, int y, Button.OnPress onPress) {
        return createSpriteIconButton(ofQoQ(CHEESE_WHEEL_TEXTURE), x, y, onPress);
    }

    /**
     * Creates a {@code YouTube} button.
     */
    public static SpriteIconButton createYouTubeButton(Screen parent, String link) {
        return createSpriteIconButton(ofQoQ(YOUTUBE_TEXTURE), 0, 0, ConfirmLinkScreen.confirmLink(parent, link, false));
    }

    /**
     * Creates a {@link SpriteIconButton}.
     */
    public static SpriteIconButton createSpriteIconButton(Identifier sprite, int x, int y, Button.OnPress onPress) {
        SpriteIconButton button = SpriteIconButton.builder(ModTexts.BLANK, onPress, false)
                .width(20)
                .sprite(sprite, 16, 16)
                .build();
        button.setPosition(x, y);
        return button;
    }

    /**
     * @return the {@code width} for a {@link AbstractModScreen} button, on the {@code left-side} of the screen.
     */
    public static int getLeftButtonPosition(int width, int leftButtonIndex) {
        return width / 2 - 113 - (leftButtonIndex * 24);
    }

    /**
     * @return the {@code width} for a {@link AbstractModScreen} button, on the {@code right-side} of the screen.
     */
    public static int getRightButtonPosition(int width, int rightButtonIndex) {
        return width / 2 + 92 + (rightButtonIndex * 24);
    }

    /**
     * @return the configuration button X position.
     */
    public static int getConfigButtonX(int width, int button) {
        if (universalOptionsInstance().getUniversal().menuButton.left()) {
            return 8 + (button * 24);
        } else if (universalOptionsInstance().getUniversal().menuButton.right()) {
            return width - 28 - (button * 24);
        } else {
            return width / 2 + 106;
        }
    }

    /**
     * @return the configuration button Y position.
     */
    public static int getConfigButtonY(int height, int button) {
        if (universalOptionsInstance().getUniversal().menuButton.left() || universalOptionsInstance().getUniversal().menuButton.right()) {
            return height - 29;
        } else {
            return height / 4 + 72 + (button * 24) - 16 + (MultiLoader.getPlatform().isNeoForged() ? -6 : 0);
        }
    }

    /**
     * @return the path for a widget.
     */
    public static String getWidgetPath(boolean forSearchBar) {
        String appended = !forSearchBar && clientOptionsInstance().getAccessibilityOptions().useLegacyTextures ? "legacy/" : "";
        return switch (clientOptionsInstance().getGeneralOptions().theme) {
            case DARK -> "dark/" + appended;
            case TRANSPARENT -> "transparent/";
            default -> "vanilla/" + appended;
        };
    }
}