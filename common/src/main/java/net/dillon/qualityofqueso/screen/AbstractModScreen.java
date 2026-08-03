package net.dillon.qualityofqueso.screen;

import net.dillon.dillonlib.platform.Platforms;
import net.dillon.qualityofqueso.helper.ButtonHelper;
import net.dillon.qualityofqueso.helper.ModHelper;
import net.dillon.qualityofqueso.platform.QualityOfQuesoPlatforms;
import net.dillon.qualityofqueso.util.KeybindScrollHelper;
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
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.LevelResource;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static net.dillon.dillonlib.task.ClientTasks.openLink;
import static net.dillon.dillonlib.task.ClientTasks.openScreen;
import static net.dillon.qualityofqueso.helper.ButtonHelper.*;
import static net.dillon.qualityofqueso.helper.ManagementHelper.buttonActive;
import static net.dillon.qualityofqueso.helper.ModHelper.*;
import static net.dillon.qualityofqueso.util.ModConstants.*;

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
     * @return an {@link AbstractWidget} from an {@link OptionInstance}.
     */
    @Deprecated
    private static AbstractWidget createOption(OptionInstance<?> instance) {
        return instance.createButton(Minecraft.getInstance().options);
    }

    /**
     * The list of {@link OptionInstance}s that should be added to the screen.
     */
    protected abstract AbstractWidget[] options();

    /**
     * Opens the keybinds screen.
     */
    protected void openKeybinds() {
        KeybindScrollHelper.requestScroll();
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
        ModHelper.debug("Saved changes.");
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

        this.screenshotsButton = this.addRenderableWidget(ButtonHelper.createSpriteIconButton(
                ofQoQ(OPEN_SCREENSHOTS_DIRECTORY_TEXTURE),
                (button) -> {
                    File screenshots = new File(Minecraft.getInstance().gameDirectory, "screenshots");
                    if (!screenshots.exists()) {
                        screenshots.mkdirs();
                    }
                    Util.getPlatform().openFile(screenshots);
                },
                Component.translatable("qualityofqueso.gui.open_screenshots_folder")
        ));

        if (this.minecraft.level != null) {
            if (this.minecraft.getSingleplayerServer() != null) {
                this.worldDirectoryButton = this.addRenderableWidget(ButtonHelper.createSpriteIconButton(
                        ofQoQ(OPEN_WORLD_DIRECTORY_TEXTURE),
                        (button) -> {
                            Path worldPath = this.minecraft.getSingleplayerServer().getWorldPath(LevelResource.ROOT);
                            Util.getPlatform().openFile(worldPath.toFile());
                        },
                        Component.translatable("qualityofqueso.gui.open_world_folder")
                ));
            }
            if (ModHelper.clientOptionsInstance().getAccessibilityOptions().eChestButton.qoqMenu() && this.minecraft.player != null) {
                this.viewLastKnownEnderChestButton = this.addRenderableWidget(ButtonHelper.createSpriteIconButton(
                        ofQoQ(ENDER_CHEST),
                        (button) -> {
                            openScreen(new EnderChestPreviewScreen());
                        },
                        Component.translatable("qualityofqueso.gui.view_ender_chest.tooltip")
                ));
            }
        } else {
            this.worldDirectoryButton = this.addRenderableWidget(ButtonHelper.createSpriteIconButton(
                    ofQoQ(OPEN_CONFIG_DIRECTORY_TEXTURE),
                    (button) -> {
                        this.openConfigDirectory();
                    },
                    Component.translatable("qualityofqueso.gui.open_config_directory")
            ));
        }

        this.wikiButton = this.addRenderableWidget(ButtonHelper.createSpriteIconButton(
                ofQoQ(WIKI_TEXTURE),
                (button) -> openLink(this, WIKI_LINK, false),
                Component.translatable("qualityofqueso.gui.learn_more")
        ));

        this.discordButton = this.addRenderableWidget(ButtonHelper.createSpriteIconButton(
                ofQoQ(DISCORD_TEXTURE),
                (button) -> openLink(this, DISCORD_LINK, false),
                Component.translatable("qualityofqueso.gui.discord")
        ));

        this.youtubeButton = this.addRenderableWidget(createYouTubeButton(this, this.youtubeLink()));
    }

    /**
     * Renders the tooltip for the done button.
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        int textWidth = this.width - 20;
        int textHeight = this.height - 21;
        int imageWidth = this.width - QualityOfQuesoPlatforms.getPlatform().logoWidth().getWidthModifier();
        int imageHeight = this.height - 26;
        graphics.centeredText(this.font, VERSION, textWidth, textHeight, CommonColors.WHITE);
        graphics.blit(RenderPipelines.GUI_TEXTURED, ofQoQ("textures/gui/sprites/" + CHEESE_WHEEL_TEXTURE + ".png"), imageWidth, imageHeight, 0.0F, 0.0F, 18, 18, 18, 18);

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
        if (event.key() == GLFW.GLFW_KEY_F4) {
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