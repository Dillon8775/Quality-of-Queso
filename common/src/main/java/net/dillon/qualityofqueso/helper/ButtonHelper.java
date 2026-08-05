package net.dillon.qualityofqueso.helper;

import net.dillon.dillonlib.task.ClientTasks;
import net.dillon.dillonlib.util.Texts;
import net.dillon.qualityofqueso.platform.QualityOfQuesoPlatforms;
import net.dillon.qualityofqueso.screen.AbstractModScreen;
import net.dillon.qualityofqueso.screen.MainMenuScreen;
import net.dillon.qualityofqueso.util.ModConstants;
import net.dillon.qualityofqueso.widget.QuesoButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Map;

import static net.dillon.dillonlib.task.ClientTasks.openLink;
import static net.dillon.dillonlib.task.ClientTasks.openScreen;
import static net.dillon.qualityofqueso.helper.ManagementHelper.getTransferButtonXY;
import static net.dillon.qualityofqueso.helper.ModHelper.*;
import static net.dillon.qualityofqueso.util.ModConstants.CHEESE_WHEEL_TEXTURE;
import static net.dillon.qualityofqueso.util.ModConstants.YOUTUBE_TEXTURE;

/**
 * Utility and helper class for buttons.
 */
public class ButtonHelper {

    /**
     * Draws the texture for a {@code inventory management button.}
     */
    public static void drawButtonTexture(GuiGraphicsExtractor graphics, String name, QuesoButton button) {
        int xy = getTransferButtonXY(button);
        graphics.blit(RenderPipelines.GUI_TEXTURED, ofQoQ("textures/gui/sprites/button/" + name + ".png"), button.getX() - 1, button.getY() - 1, 0.0F, 0.0F, xy, xy, xy, xy);
    }

    /**
     * Creates the main Quality of Queso menu button.
     */
    public static SpriteIconButton createMenuButton(Button.OnPress onPress, boolean tooltip) {
        return ClientTasks.createMenuButton(ofQoQ(CHEESE_WHEEL_TEXTURE), onPress,
                Map.of(ModConstants.HAS_UPDATE, Component.translatable("qualityofqueso.gui.update_available")),
                Component.translatable("qualityofqueso.gui.options.title"),
                tooltip);
    }

    /**
     * Creates the main Quality of Queso menu button.
     */
    public static SpriteIconButton createMainMenuButton(Screen parent) {
        return createMenuButton(onPress -> openScreen(new MainMenuScreen(parent)), true);
    }

    /**
     * Creates the blacklist server button.
     */
    public static SpriteIconButton createBlacklistServerButton(String address) {
        return createMenuButton(onPress -> {
            if (universalOptionsInstance().blacklistedServers.contains(address)) {
                universalOptionsInstance().blacklistedServers.remove(address);
            } else {
                universalOptionsInstance().blacklistedServers.add(address);
            }
            saveAndApplyConfigs(Minecraft.getInstance());
        }, false);
    }

    /**
     * Creates a {@code YouTube} button.
     */
    public static SpriteIconButton createYouTubeButton(Screen parent, String link) {
        return ClientTasks.createSpriteIconButton(ofQoQ(YOUTUBE_TEXTURE), (button) -> openLink(parent, link, false),
                Component.translatable("qualityofqueso.gui.showcase.main.tooltip"));
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
        if (universalOptionsInstance().menuButton.left()) {
            return 8 + (button * 24);
        } else if (universalOptionsInstance().menuButton.right()) {
            return width - 28 - (button * 24);
        } else {
            return width / 2 + 106;
        }
    }

    /**
     * @return the configuration button Y position.
     */
    public static int getConfigButtonY(int height, int button) {
        if (universalOptionsInstance().menuButton.left() || universalOptionsInstance().menuButton.right()) {
            return height - 29;
        } else {
            return height / 4 + 72 + (button * 24) - 16 + (QualityOfQuesoPlatforms.getPlatform().platformName().neoforge() ? -6 : 0);
        }
    }

    /**
     * @return the path for a widget.
     */
    public static String getWidgetPath(boolean forSearchBar) {
        String appended = !forSearchBar && clientOptionsInstance().getAccessibilityOptions().useLegacyTextures ? "legacy/" : "";
        return switch (clientOptionsInstance().getGeneralOptions().theme) {
            case DARK, TRUE_DARK -> "dark/" + appended;
            case TRANSPARENT -> "transparent/";
            default -> "vanilla/" + appended;
        };
    }
}