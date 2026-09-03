package net.dillon.qualityofqueso.helper;

import net.dillon.dillonlib.platform.info.UpdatableSpriteButton;
import net.dillon.dillonlib.task.ClientTasks;
import net.dillon.qualityofqueso.platform.QualityOfQuesoPlatforms;
import net.dillon.qualityofqueso.screen.AbstractModScreen;
import net.dillon.qualityofqueso.screen.MainMenuScreen;
import net.dillon.qualityofqueso.widget.QuesoButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Map;

import static net.dillon.dillonlib.task.ClientTasks.*;
import static net.dillon.qualityofqueso.helper.ManagementHelper.getTransferButtonXY;
import static net.dillon.qualityofqueso.helper.ModConstants.CHEESE_WHEEL_TEXTURE;
import static net.dillon.qualityofqueso.helper.ModConstants.YOUTUBE_TEXTURE;
import static net.dillon.qualityofqueso.helper.ModHelper.qoqIdentifier;
import static net.dillon.qualityofqueso.helper.ModHelper.saveAndApplyConfigs;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.universal;

/**
 * Utility and helper class for buttons.
 */
public class ButtonHelper {

    /**
     * Draws the texture for a {@code inventory management button.}
     */
    public static void drawButtonTexture(GuiGraphicsExtractor graphics, String name, QuesoButton button) {
        int xy = getTransferButtonXY(button);
        blitTexture(
                graphics,
                qoqIdentifier("textures/gui/sprites/button/" + name + ".png"),
                button.getX() - 1,
                button.getY() - 1,
                xy,
                xy
        );
    }

    /**
     * Creates the main Quality of Queso menu button.
     */
    public static UpdatableSpriteButton createMenuButton(Button.OnPress onPress, boolean tooltip) {
        return ClientTasks.createMenuButton(
                "Quality of Queso Main Menu",
                qoqIdentifier(CHEESE_WHEEL_TEXTURE),
                onPress,
                Map.of(
                        ModConstants.HAS_UPDATE,
                        Component.translatable("qualityofqueso.gui.update_available")
                ),
                Component.translatable("qualityofqueso.menu.title"),
                tooltip
        );
    }

    /**
     * Creates the main Quality of Queso menu button.
     */
    public static UpdatableSpriteButton createMainMenuButton(Screen parent) {
        return createMenuButton(onPress -> openScreen(new MainMenuScreen(parent)), true);
    }

    /**
     * Creates the blacklist server button.
     */
    public static SpriteIconButton createBlacklistServerButton(String address) {
        return createMenuButton(onPress -> {
            if (universal().blacklistedServers.contains(address)) {
                universal().blacklistedServers.remove(address);
            } else {
                universal().blacklistedServers.add(address);
            }
            saveAndApplyConfigs(Minecraft.getInstance());
        }, false);
    }

    /**
     * Creates a {@code YouTube} button.
     */
    public static SpriteIconButton createYouTubeButton(Screen parent, String link) {
        return ClientTasks.createSpriteIconButton("YouTube Button", qoqIdentifier(YOUTUBE_TEXTURE), (button) -> openLink(parent, link, false),
                Component.translatable("qualityofqueso.gui.showcase.main.tooltip"), 16, 16, false);
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
        if (universal().menuButton.left()) {
            return 8 + (button * 24);
        } else if (universal().menuButton.right()) {
            return width - 28 - (button * 24);
        } else {
            return width / 2 + 106;
        }
    }

    /**
     * @return the configuration button Y position.
     */
    public static int getConfigButtonY(int height, int button) {
        if (universal().menuButton.left() || universal().menuButton.right()) {
            return height - 29;
        } else {
            return height / 4 + 72 + (button * 24) - 16 + (QualityOfQuesoPlatforms.getPlatform().platformName().neoforge() ? -6 : 0);
        }
    }

    /**
     * @return the path for a widget.
     */
    public static String getWidgetPath(boolean forSearchBar) {
        String appended = !forSearchBar && client().accessibility().useLegacyTextures ? "legacy/" : "";
        return switch (client().general().theme) {
            case DARK, TRUE_DARK -> "dark/" + appended;
            case TRANSPARENT -> "transparent/";
            default -> "vanilla/" + appended;
        };
    }
}