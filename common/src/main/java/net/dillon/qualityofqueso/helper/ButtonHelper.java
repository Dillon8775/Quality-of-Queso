package net.dillon.qualityofqueso.helper;

import net.dillon.qualityofqueso.platform.MultiLoader;
import net.dillon.qualityofqueso.screen.option.AbstractModOptionsScreen;
import net.dillon.qualityofqueso.widget.TransferButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static net.dillon.qualityofqueso.helper.ManagementHelper.getTransferButtonXY;
import static net.dillon.qualityofqueso.helper.ModHelper.*;
import static net.dillon.qualityofqueso.util.ModConstants.CHEESE_WHEEL_BUTTON_TEXTURE;
import static net.dillon.qualityofqueso.util.ModConstants.YOUTUBE_TEXTURE;

/**
 * Utility and helper class for buttons.
 */
public class ButtonHelper {

    /**
     * @return if the itemstack is a shulker.
     */
    public static boolean isStackShulker(ItemStack stack) {
        for (Item item : shulkerBoxes) {
            if (stack.is(item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Draws the texture for a {@code inventory management button.}
     */
    public static void drawButtonTexture(GuiGraphics graphics, String name, TransferButton button) {
        int xy = getTransferButtonXY(button);
        graphics.blit(new ResourceLocation("qualityofqueso:textures/gui/button/" + name + ".png"), button.getX() - 1, button.getY() - 1, 0.0F, 0.0F, xy, xy, xy, xy);
    }

    /**
     * Draws a texture over a button.
     */
    public static void drawTexture(GuiGraphics graphics, String name, Button button) {
        graphics.blit(new ResourceLocation("qualityofqueso:textures/gui/" + name + ".png"), button.getX() + 2, button.getY() + 2, 0.0F, 0.0F, 16, 16, 16, 16);
    }

    /**
     * Creates the Quality of Queso {@code menu button.}
     */
    public static ImageButton createMenuButton(int x, int y, Button.OnPress onPress) {
        return createSpriteIconButton(ofQoQ(CHEESE_WHEEL_BUTTON_TEXTURE), x, y, onPress);
    }

    /**
     * Creates a {@code YouTube} button.
     */
    public static ImageButton createYouTubeButton(Screen parent, String link) {
        return createSpriteIconButton(ofQoQ(YOUTUBE_TEXTURE), 0, 0, ConfirmLinkScreen.confirmLink(link, parent, false));
    }

    /**
     * Creates a {@link ImageButton}.
     */
    public static ImageButton createSpriteIconButton(ResourceLocation sprite, int x, int y, Button.OnPress onPress) {
        ImageButton button = new ImageButton(20, 20, 20, 20, 0, 0, 20, sprite, 20, 40, onPress);
        button.setPosition(x, y);
        return button;
    }

    /**
     * @return the {@code width} for a {@link AbstractModOptionsScreen} button, on the {@code left-side} of the screen.
     */
    public static int getLeftButtonPosition(int width, int leftButtonIndex) {
        return width / 2 - 113 - (leftButtonIndex * 24);
    }

    /**
     * @return the {@code width} for a {@link AbstractModOptionsScreen} button, on the {@code right-side} of the screen.
     */
    public static int getRightButtonPosition(int width, int rightButtonIndex) {
        return width / 2 + 92 + (rightButtonIndex * 24);
    }

    /**
     * @return the configuration button X position.
     */
    public static int getConfigButtonX(int width, int button) {
        if (uoptions().main.menuButton.left()) {
            return 8 + (button * 24);
        } else if (uoptions().main.menuButton.right()) {
            return width - 28 - (button * 24);
        } else {
            return width / 2 + 106;
        }
    }

    /**
     * @return the configuration button Y position.
     */
    public static int getConfigButtonY(int height, int button) {
        if (uoptions().main.menuButton.left() || uoptions().main.menuButton.right()) {
            return height - 29;
        } else {
            return height / 4 + 72 + (button * 24) - 16 + (MultiLoader.getPlatform().isForge() ? -6 : 0);
        }
    }

    /**
     * @return the path for a widget.
     */
    public static String getWidgetPath() {
        return switch (options().accessibility.widgetTheme) {
            case DARK -> "dark/";
            case TRANSPARENT -> "transparent/";
            default -> "vanilla/";
        };
    }
}