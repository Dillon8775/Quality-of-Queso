package net.dillon.qualityofqueso.widget;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.dillonlib.client.component.SoundBuilder;
import net.dillon.dillonlib.client.component.SpriteIconButtonWithSound;
import net.dillon.dillonlib.client.component.TextureBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ScaledWidgetSprites;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static net.dillon.dillonlib.task.ClientTasks.*;
import static net.dillon.qualityofqueso.helper.ModConstants.*;
import static net.dillon.qualityofqueso.option.ClientOptionInstances.client;

/**
 * A button to manage the level you are playing on.
 */
public class ManageConfigAndLevelButton extends SpriteIconButtonWithSound {

    public ManageConfigAndLevelButton(Button.OnPress onPress) {
        super(
                20,
                20,
                null,
                new ScaledWidgetSprites(new WidgetSprites(getSprite()), 16, 16),
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
            SoundBuilder.create(SoundEvents.BOOK_PAGE_TURN.value())
                    .volume(0.8F)
                    .buildAndPlay();
        } else {
            super.playDownSound(soundManager);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClicked) {
        if (hasShiftDown()) {
            backupConfigs(CONFIGS);
            Blaze3D.openPath(BACKUP_CONFIG_DIR);
            return false;
        }

        if (!isCurrentLevelNull() && event.button() == InputConstants.MOUSE_BUTTON_RIGHT && this.isHovered()) {
            openConfigDirectory();
            return true;
        }

        return super.mouseClicked(event, doubleClicked);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics) {
        super.extractContents(graphics);

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
        }

        this.setTooltip(Tooltip.create(tooltip()));
    }

    /**
     * Opens the config directory for the mod.
     */
    public static void openConfigDirectory() {
        Blaze3D.openPath(CONFIG_DIR);
    }

    /**
     * Backs up all mod configs.
     */
    public static void backupConfigs(List<String> configs) {
        try {
            Files.createDirectories(BACKUP_CONFIG_DIR);

            for (String config : configs) {
                Files.copy(
                        CONFIG_DIR.resolve(config),
                        BACKUP_CONFIG_DIR.resolve(config),
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            executeIfClientPlayer(localPlayer -> localPlayer.sendSystemMessage(Component.translatable("qualityofqueso.backed_up")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the tooltip for this button.
     */
    private Component tooltip() {
        Component backupConfig = Component.translatable("qualityofqueso.shift_to_backup_config").withStyle(ChatFormatting.DARK_GRAY);
        Component rightClickOpenConfigDirectory = Component.translatable("qualityofqueso.right_click_open_config_directory").withStyle(ChatFormatting.GRAY);

        if (isOnServer()) {
            Component finalTooltip = Component.translatable("qualityofqueso.gui.blacklisted_server_button_prefix")
                    .copy().append(
                            isServerBlacklisted(getCurrentServerAddress()) ?
                                    Component.translatable("qualityofqueso.gui.remove_blacklisted_server") :
                                    Component.translatable("qualityofqueso.gui.add_blacklisted_server")
                    );

            return finalTooltip
                    .copy().append(rightClickOpenConfigDirectory)
                    .copy().append(Component.literal("\n"))
                    .copy().append(backupConfig);
        }

        if (isCurrentLevelNull()) {
            return Component.translatable("qualityofqueso.open_config_directory")
                    .copy().append(Component.literal("\n"))
                    .copy().append(backupConfig);
        } else {
            return Component.translatable("qualityofqueso.open_world_folder")
                    .copy().append(Component.literal("\n"))
                    .copy().append(rightClickOpenConfigDirectory)
                    .copy().append(Component.literal("\n"))
                    .copy().append(backupConfig);
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
        return client().general().blacklistedServers.contains(serverAddress);
    }
}