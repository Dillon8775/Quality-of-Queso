package net.dillon.qualityofqueso.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.dillonlib.platform.Platforms;
import net.dillon.dillonlib.task.ClientTasks;
import net.dillon.dillonlib.util.KeybindScrollHelper;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.keybind.ModKeyMappings;
import net.dillon.qualityofqueso.platform.QualityOfQuesoPlatforms;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static net.dillon.dillonlib.task.ClientTasks.*;
import static net.dillon.qualityofqueso.helper.ButtonHelper.*;
import static net.dillon.qualityofqueso.helper.ManagementHelper.buttonActive;
import static net.dillon.qualityofqueso.helper.ModConstants.*;
import static net.dillon.qualityofqueso.helper.ModHelper.*;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * A basic screen for Quality of Queso.
 */
public abstract class AbstractModScreen extends OptionsSubScreen {
    private Button doneButton;
    private SpriteIconButton viewLastKnownEnderChestButton, worldDirectoryButton, screenshotsButton, discordButton, wikiButton;
    protected SpriteIconButton youtubeButton;

    public AbstractModScreen(Screen parent, Component title) {
        super(parent, Minecraft.getInstance().options, title);
    }

    /**
     * The list of {@link OptionInstance}s that should be added to the screen.
     */
    protected abstract AbstractWidget[] options();

    /**
     * Opens the keybinds screen.
     */
    protected void openKeybinds() {
        KeybindScrollHelper.request(ModKeyMappings.QOQ_KEY_CATEGORY);
        openScreen(new KeyBindsScreen(this, this.options));
    }

    /**
     * Determines if buttons can be active, and renders custom tooltips on them.
     */
    protected void activateButtons() {
    }

    /**
     * @return the showcase video link to use.
     */
    private String youtubeLink() {
        return SHOWCASE_VIDEO_LINK;
    }

    /**
     * Opens the config directory.
     */
    private void openConfigDirectory() {
        Util.getPlatform().openFile(Platforms.getCommonPlatform().configDir().resolve("qualityofqueso").toFile());
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
    protected void init() {
        super.init();
        this.list.addSmall(List.of(this.options()));
    }

    @Override
    protected void addFooter() {
        this.doneButton = this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, button -> {
            this.onClose();
        }).width(175).build());

        this.screenshotsButton = this.addRenderableWidget(ClientTasks.createSpriteIconButton(
                "Open Screenshots Button",
                qoqIdentifier(OPEN_SCREENSHOTS_DIRECTORY_TEXTURE),
                (button) -> {
                    File screenshots = new File(Minecraft.getInstance().gameDirectory, "screenshots");
                    if (!screenshots.exists()) {
                        screenshots.mkdirs();
                    }
                    Util.getPlatform().openFile(screenshots);
                },
                Component.translatable("qualityofqueso.gui.open_screenshots_folder"),
                false
        ));

        if (this.minecraft.level != null) {
            if (this.minecraft.getSingleplayerServer() != null) {
                this.worldDirectoryButton = this.addRenderableWidget(ClientTasks.createSpriteIconButton(
                        "Open World Directory Button",
                        qoqIdentifier(OPEN_WORLD_DIRECTORY_TEXTURE),
                        (button) -> {
                            Path worldPath = this.minecraft.getSingleplayerServer().getWorldPath(LevelResource.ROOT);
                            Util.getPlatform().openFile(worldPath.toFile());
                        },
                        Component.translatable("qualityofqueso.gui.open_world_folder"),
                        false
                ));
            }
            if (client().accessibility().eChestButton.qoqMenu()) {
                executeIfClientPlayer(localPlayer -> {
                    this.viewLastKnownEnderChestButton = this.addRenderableWidget(ClientTasks.createSpriteIconButton(
                            "View Last Known Ender Chest Button",
                            qoqIdentifier(ENDER_CHEST),
                            (button) -> {
                                openScreen(new EnderChestPreviewScreen());
                            },
                            Component.translatable("qualityofqueso.gui.view_ender_chest.tooltip"),
                            false
                    ));
                });
            }
        } else {
            this.worldDirectoryButton = this.addRenderableWidget(ClientTasks.createSpriteIconButton(
                    "Open Config Directory Button",
                    qoqIdentifier(OPEN_CONFIG_DIRECTORY_TEXTURE),
                    (button) -> {
                        this.openConfigDirectory();
                    },
                    Component.translatable("qualityofqueso.gui.open_config_directory"),
                    false
            ));
        }

        this.wikiButton = this.addRenderableWidget(ClientTasks.createSpriteIconButton(
                "Wiki Button",
                qoqIdentifier(WIKI_TEXTURE),
                (button) -> openLink(this, WIKI_LINK, false),
                Component.translatable("qualityofqueso.gui.learn_more"),
                false
        ));

        this.discordButton = this.addRenderableWidget(ClientTasks.createSpriteIconButton(
                "Discord Button",
                qoqIdentifier(DISCORD_TEXTURE),
                (button) -> openLink(this, DISCORD_LINK, false),
                Component.translatable("qualityofqueso.gui.discord"),
                false
        ));

        this.youtubeButton = this.addRenderableWidget(createYouTubeButton(this, this.youtubeLink()));
    }

    /**
     * Renders the tooltip for the done button.
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        ClientTasks.drawModInfo(
                graphics,
                this,
                VERSION,
                QualityOfQuesoPlatforms.getPlatform().logoWidth().getWidthModifier(),
                qoqIdentifier("textures/gui/sprites/" + CHEESE_WHEEL_TEXTURE + ".png"),
                HAS_UPDATE
        );

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

    @Override
    public boolean keyPressed(final KeyEvent event) {
        if (event.key() == InputConstants.KEY_F4) {
            this.openConfigDirectory();
            return true;
        }
        return super.keyPressed(event);
    }

    /**
     * Required method.
     */
    @Override
    protected void addOptions() {
    }
}