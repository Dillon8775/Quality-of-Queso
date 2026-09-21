package net.dillon.qualityofqueso.screen;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.dillonlib.platform.Platforms;
import net.dillon.dillonlib.platform.info.UpdatableSpriteButton;
import net.dillon.dillonlib.screen.DillonLibMenuScreen;
import net.dillon.dillonlib.screen.ScreenBuilder;
import net.dillon.dillonlib.task.ClientTasks;
import net.dillon.dillonlib.util.KeybindScrollHelper;
import net.dillon.dillonlib.util.Links;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.keybind.ModKeyMappings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.nio.file.Path;

import static net.dillon.dillonlib.task.ClientTasks.openLink;
import static net.dillon.dillonlib.task.ClientTasks.openScreen;
import static net.dillon.qualityofqueso.helper.ButtonHelper.*;
import static net.dillon.qualityofqueso.helper.ModConstants.*;
import static net.dillon.qualityofqueso.helper.ModHelper.*;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * An abstract screen for Quality of Queso.
 */
public class AbstractModScreen extends DillonLibMenuScreen {

    public AbstractModScreen(Screen parent, Component title) {
        super(parent, title, ScreenBuilder::ofBottomCentered);
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
        Button doneButton = this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, button -> {
            this.onClose();
        }).width(175).build());

        this.addRenderableWidget(
                this.createWidget(
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
                        ),
                        () -> getLeftButtonPosition(this.width, 0),
                        doneButton::getY
                )
        );

        if (this.minecraft.level != null) {
            if (this.minecraft.getSingleplayerServer() != null) {

                this.addRenderableWidget(
                        this.createWidget(
                                UpdatableSpriteButton.ofDefault(
                                        "Open World Directory Button",
                                        qoqIdentifier(OPEN_WORLD_DIRECTORY_TEXTURE),
                                        (button) -> {
                                            Path worldPath = this.minecraft.getSingleplayerServer().getWorldPath(LevelResource.ROOT);
                                            Blaze3D.openPath(worldPath);
                                        },
                                        Component.translatable("qualityofqueso.open_world_folder"),
                                        false
                                ),
                                () -> getLeftButtonPosition(this.width, 1),
                                doneButton::getY
                        )
                );

            }

            if (client().accessibility().eChestButton.qoqMenu()) {
                if (this.minecraft.player != null) {

                    this.addRenderableWidget(
                            this.createWidget(
                                    UpdatableSpriteButton.ofDefault(
                                            "View Last Known Ender Chest Button",
                                            qoqIdentifier(ENDER_CHEST),
                                            (button) -> {
                                                openScreen(new EnderChestPreviewScreen());
                                            },
                                            Component.translatable("qualityofqueso.gui.view_ender_chest.tooltip"),
                                            false
                                    ),
                                    modEnabled(this.minecraft),
                                    () -> getLeftButtonPosition(this.width, 2),
                                    doneButton::getY
                            )
                    );

                }
            }
        } else {
            this.addRenderableWidget(
                    this.createWidget(
                            UpdatableSpriteButton.ofDefault(
                                    "Open Config Directory Button",
                                    qoqIdentifier(OPEN_CONFIG_DIRECTORY_TEXTURE),
                                    (button) -> {
                                        this.openConfigDirectory();
                                    },
                                    Component.translatable("qualityofqueso.open_config_directory"),
                                    false
                            ),
                            () -> getLeftButtonPosition(this.width, 1),
                            doneButton::getY
                    )
            );
        }

        this.addRenderableWidget(
                this.createWidget(
                        UpdatableSpriteButton.ofDefault(
                                "Wiki Button",
                                qoqIdentifier(WIKI_TEXTURE),
                                (button) -> openLink(this, WIKI_LINK, false),
                                Component.translatable("qualityofqueso.wiki"),
                                false
                        ),
                        () -> getRightButtonPosition(this.width, 0),
                        doneButton::getY
                )
        );

        this.addRenderableWidget(
                this.createWidget(
                        UpdatableSpriteButton.ofDefault(
                                "Discord Button",
                                qoqIdentifier(DISCORD_TEXTURE),
                                (button) -> openLink(this, Links.DILLONS_DISCORD, false),
                                Component.translatable("qualityofqueso.discord"),
                                false
                        ),
                        () -> getRightButtonPosition(this.width, 1),
                        doneButton::getY
                )
        );

        this.addRenderableWidget(
                this.createWidget(
                        createYouTubeButton(this, SHOWCASE_VIDEO_LINK),
                        () -> getRightButtonPosition(this.width, 2),
                        doneButton::getY
                )
        );
    }
}