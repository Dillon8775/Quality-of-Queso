package net.dillon.qualityofqueso.widget;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.dillonlib.client.component.SoundBuilder;
import net.dillon.dillonlib.client.component.SpriteIconButtonWithSound;
import net.dillon.dillonlib.client.component.TextureBuilder;
import net.dillon.dillonlib.platform.Platforms;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;

import static net.dillon.dillonlib.task.ClientTasks.*;
import static net.dillon.qualityofqueso.helper.ModConstants.*;
import static net.dillon.qualityofqueso.option.OptionInstances.universal;

/**
 * A button to manage the level you are playing on.
 */
public class ManageLevelButton extends SpriteIconButtonWithSound {

    public ManageLevelButton(Button.OnPress onPress) {
        super(
                20,
                20,
                16,
                16,
                null,
                new WidgetSprites(getSprite()),
                Component::empty,
                onPress
        );
    }

    /**
     * @return the sprite for this button.
     */
    private static Identifier getSprite() {
        if (isOnServer()) {
            return CHEESE_WHEEL_TEXTURE;
        } else if (!isCurrentLevelNull()) {
            return OPEN_WORLD_DIRECTORY;
        }

        return OPEN_CONFIG_DIRECTORY;
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        if (!isOnServer() && !isCurrentLevelNull()) {
            SoundBuilder.create(SoundEvents.BOOK_PAGE_TURN)
                    .volume(0.8F)
                    .buildAndPlay();
        } else {
            super.playDownSound(soundManager);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClicked) {
        if (!isCurrentLevelNull() && event.button() == InputConstants.MOUSE_BUTTON_RIGHT && this.isHoveredOrFocused()) {
            openConfigDirectory();
            return true;
        }

        return super.mouseClicked(event, doubleClicked);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractContents(graphics, mouseX, mouseY, a);

        if (isOnServer()) {
            TextureBuilder.create(
                    graphics,
                    isServerBlacklisted(getCurrentServerAddress())
                            ? DISABLED_TEXTURE
                            : ENABLED_TEXTURE,
                    this.getX(),
                    this.getY(),
                    this.getWidth(),
                    this.getHeight()
            ).buildAndBlit(true);

            if (universal().multiServerConfigs) {
                TextureBuilder.create(
                        graphics,
                        MULTI_CONFIG_TEXTURE,
                                this.getX() - 2,
                                this.getY() - 1,
                                16,
                                16
                ).buildAndBlit(true);
            }
        }

        this.setTooltip(Tooltip.create(tooltip()));
    }

    /**
     * Opens the config directory for the mod.
     */
    public static void openConfigDirectory() {
        Blaze3D.openPath(
                Platforms.getUniversalPlatform().configDir().resolve("qualityofqueso")
                        .toFile().toPath()
        );
    }

    /**
     * @return the tooltip for this button.
     */
    private Component tooltip() {
        Component rightClickOpenConfigDirectory = Component.translatable("qualityofqueso.right_click_open_config_directory").withStyle(ChatFormatting.GRAY);

        if (isOnServer()) {
            Component finalTooltip = Component.translatable("qualityofqueso.gui.blacklisted_server_button_prefix")
                    .copy().append(
                            isServerBlacklisted(getCurrentServerAddress()) ?
                                    Component.translatable("qualityofqueso.gui.remove_blacklisted_server") :
                                    Component.translatable("qualityofqueso.gui.add_blacklisted_server")
                    );

            if (universal().multiServerConfigs) {
                finalTooltip = finalTooltip
                        .copy().append(
                                Component.translatable("qualityofqueso.gui.multi_server_configs_enabled")
                        );
            }

            return finalTooltip
                    .copy().append(rightClickOpenConfigDirectory);
        }

        if (isCurrentLevelNull()) {
            return Component.translatable("qualityofqueso.open_config_directory");
        } else {
            return Component.translatable("qualityofqueso.open_world_folder")
                    .copy().append(Component.literal("\n"))
                    .copy().append(rightClickOpenConfigDirectory);
        }
    }

    /**
     * @return the current server for the player.
     */
    public static String getCurrentServerAddress() {
        return isOnServer()
                ? getMinecraft().getCurrentServer().ip
                : "";
    }

    /**
     * Checks if a server is blacklisted or not.
     */
    private static boolean isServerBlacklisted(String serverAddress) {
        return universal().blacklistedServers.contains(serverAddress);
    }
}