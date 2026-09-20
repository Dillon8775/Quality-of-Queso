package net.dillon.qualityofqueso.screen;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.dillonlib.platform.Platforms;
import net.dillon.dillonlib.platform.info.UpdatableSpriteButton;
import net.dillon.dillonlib.screen.DillonLibScreen;
import net.dillon.dillonlib.task.ClientTasks;
import net.dillon.dillonlib.util.KeybindScrollHelper;
import net.dillon.dillonlib.util.Links;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.keybind.ModKeyMappings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.nio.file.Path;

import static net.dillon.dillonlib.task.ClientTasks.*;
import static net.dillon.qualityofqueso.helper.ButtonHelper.*;
import static net.dillon.qualityofqueso.helper.ManagementHelper.buttonActive;
import static net.dillon.qualityofqueso.helper.ModConstants.*;
import static net.dillon.qualityofqueso.helper.ModHelper.*;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * An abstract screen for Quality of Queso.
 */
public class AbstractModScreen extends DillonLibScreen {
    private Button doneButton;
    private SpriteIconButton viewLastKnownEnderChestButton, worldDirectoryButton, screenshotsButton, discordButton, wikiButton;
    protected SpriteIconButton youtubeButton;

    public AbstractModScreen(Screen parent, Component title) {
        super(parent, title);
    }

    @Override
    protected void renderModInfo(GuiGraphicsExtractor graphics) {
        ClientTasks.drawModInfo(
                graphics,
                this,
                VERSION,
                qoqIdentifier(CHEESE_WHEEL_TEXTURE),
                HAS_UPDATE
        );
    }

    @Override
    public void openConfigDirectory() {
        Blaze3D.openPath(
                Platforms.getCommonPlatform().configDir().resolve("qualityofqueso")
                        .toFile().toPath()
        );
    }

    @Override
    protected void openKeybinds() {
        KeybindScrollHelper.request(ModKeyMappings.QOQ_KEY_CATEGORY);
        openScreen(new KeyBindsScreen(this, this.options));
    }

    @Override
    public void onClose() {
        saveAndApplyConfigs(this.minecraft);
        ModConstants.LOGGER.debug("Saved changes.");
        if (this.minecraft.level != null) {
            sendClientPreferencesToServer();
        }
        super.onClose();
    }

    @Override
    public boolean keyPressed(final KeyEvent event) {
        if (event.key() == InputConstants.KEY_F4) {
            this.openConfigDirectory();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    protected void addFooter() {
        this.doneButton = this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, button -> {
            this.onClose();
        }).width(175).build());

        this.screenshotsButton = this.addRenderableWidget(
                UpdatableSpriteButton.ofDefault(
                        "Open Screenshots Button",
                        qoqIdentifier(OPEN_SCREENSHOTS_DIRECTORY_TEXTURE),
                        (button) -> {
                            File screenshots = new File(Minecraft.getInstance().gameDirectory, "screenshots");
                            if (!screenshots.exists()) {
                                screenshots.mkdirs();
                            }
                            Blaze3D.openPath(screenshots.toPath());
                        },
                        Component.translatable("qualityofqueso.open_screenshots_folder"),
                        false
                ));

        if (this.minecraft.level != null) {
            if (this.minecraft.getSingleplayerServer() != null) {
                this.worldDirectoryButton = this.addRenderableWidget(
                        UpdatableSpriteButton.ofDefault(
                                "Open World Directory Button",
                                qoqIdentifier(OPEN_WORLD_DIRECTORY_TEXTURE),
                                (button) -> {
                                    Path worldPath = this.minecraft.getSingleplayerServer().getWorldPath(LevelResource.ROOT);
                                    Blaze3D.openPath(worldPath);
                                },
                                Component.translatable("qualityofqueso.open_world_folder"),
                                false
                        )
                );
            }
            if (client().accessibility().eChestButton.qoqMenu()) {
                executeIfClientPlayer(localPlayer -> {
                    this.viewLastKnownEnderChestButton = this.addRenderableWidget(
                            UpdatableSpriteButton.ofDefault(
                                    "View Last Known Ender Chest Button",
                                    qoqIdentifier(ENDER_CHEST),
                                    (button) -> {
                                        openScreen(new EnderChestPreviewScreen());
                                    },
                                    Component.translatable("qualityofqueso.gui.view_ender_chest.tooltip"),
                                    false
                            )
                    );
                });
            }
        } else {
            this.worldDirectoryButton = this.addRenderableWidget(
                    UpdatableSpriteButton.ofDefault(
                            "Open Config Directory Button",
                            qoqIdentifier(OPEN_CONFIG_DIRECTORY_TEXTURE),
                            (button) -> {
                                this.openConfigDirectory();
                            },
                            Component.translatable("qualityofqueso.open_config_directory"),
                            false
                    )
            );
        }

        this.wikiButton = this.addRenderableWidget(
                UpdatableSpriteButton.ofDefault(
                        "Wiki Button",
                        qoqIdentifier(WIKI_TEXTURE),
                        (button) -> openLink(this, WIKI_LINK, false),
                        Component.translatable("qualityofqueso.wiki"),
                        false
                )
        );

        this.discordButton = this.addRenderableWidget(
                UpdatableSpriteButton.ofDefault(
                        "Discord Button",
                        qoqIdentifier(DISCORD_TEXTURE),
                        (button) -> openLink(this, Links.DILLONS_DISCORD, false),
                        Component.translatable("qualityofqueso.discord"),
                        false
                )
        );

        this.youtubeButton = this.addRenderableWidget(createYouTubeButton(this, SHOWCASE_VIDEO_LINK));
    }

    /**
     * Renders the tooltip for the done button.
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        int leftIndex = 0;
        if (buttonActive(this.screenshotsButton)) {
            this.screenshotsButton.setPosition(getLeftButtonPosition(this.width, leftIndex), this.doneButton.getY());
            leftIndex++;
        }

        if (buttonActive(this.worldDirectoryButton)) {
            this.worldDirectoryButton.setPosition(getLeftButtonPosition(this.width, leftIndex), this.doneButton.getY());
            leftIndex++;
        }

        if (this.viewLastKnownEnderChestButton != null) {
            this.viewLastKnownEnderChestButton.active = modEnabled(this.minecraft);
            this.viewLastKnownEnderChestButton.setPosition(getLeftButtonPosition(this.width, leftIndex), this.doneButton.getY());
        }

        int rightIndex = 0;
        if (buttonActive(this.wikiButton)) {
            this.wikiButton.setPosition(getRightButtonPosition(this.width, rightIndex), this.doneButton.getY());
            rightIndex++;
        }

        if (buttonActive(this.discordButton)) {
            this.discordButton.setPosition(getRightButtonPosition(this.width, rightIndex), this.doneButton.getY());
            rightIndex++;
        }

        if (buttonActive(this.youtubeButton)) {
            this.youtubeButton.setPosition(getRightButtonPosition(this.width, rightIndex), this.doneButton.getY());
        }

        this.activateButtons();
        super.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
    }
}