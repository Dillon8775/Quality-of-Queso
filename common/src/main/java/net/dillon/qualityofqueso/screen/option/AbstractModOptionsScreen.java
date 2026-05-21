package net.dillon.qualityofqueso.screen.option;

import net.dillon.qualityofqueso.helper.ButtonHelper;
import net.dillon.qualityofqueso.helper.ModHelper;
import net.dillon.qualityofqueso.platform.MultiLoader;
import net.dillon.qualityofqueso.screen.EnderChestPreviewScreen;
import net.dillon.qualityofqueso.util.KeybindScrollHelper;
import net.dillon.qualityofqueso.util.VersionType;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static net.dillon.qualityofqueso.helper.ButtonHelper.*;
import static net.dillon.qualityofqueso.helper.GuiHelper.drawTooltip;
import static net.dillon.qualityofqueso.helper.ManagementHelper.buttonActive;
import static net.dillon.qualityofqueso.helper.ModHelper.*;
import static net.dillon.qualityofqueso.util.ModConstants.*;

public abstract class AbstractModOptionsScreen extends OptionsSubScreen {
    private Button doneButton;
    private SpriteIconButton viewEnderChestButton, worldDirectoryButton, screenshotsButton, discordButton, wikiButton;
    protected SpriteIconButton youtubeButton;

    public AbstractModOptionsScreen(Screen parent, Component title) {
        super(parent, Minecraft.getInstance().options, title);
    }

    /**
     * @return an {@link AbstractWidget} from an {@link OptionInstance}.
     */
    protected static AbstractWidget createOption(OptionInstance<?> instance) {
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
        this.minecraft.setScreen(new KeyBindsScreen(this, this.options));
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
        if (this.addOptionsByDefault()) {
            this.list.addSmall(List.of(this.options()));
        }
    }

    @Override
    protected void addFooter() {
        this.doneButton = this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, button -> {
            if (Screen.hasShiftDown()) {
                Util.getPlatform().openFile(MultiLoader.getPlatform().getConfigDir().resolve(ROOT_CONFIG_DIR).toFile());
            } else {
                this.onClose();
            }
        }).width(175).build());

        this.screenshotsButton = this.addRenderableWidget(ButtonHelper.createSpriteIconButton(
                ofQoQ(OPEN_SCREENSHOTS_DIRECTORY_TEXTURE),
                0,
                0,
                (button) -> {
                    File screenshots = new File(Minecraft.getInstance().gameDirectory, "screenshots");
                    if (!screenshots.exists()) {
                        screenshots.mkdirs();
                    }
                    Util.getPlatform().openFile(screenshots);
                }
        ));

        if (this.minecraft.level != null) {
            if (this.minecraft.getSingleplayerServer() != null) {
                this.worldDirectoryButton = this.addRenderableWidget(ButtonHelper.createSpriteIconButton(
                        ofQoQ(OPEN_WORLD_DIRECTORY_TEXTURE),
                        0,
                        0,
                        (button) -> {
                            Path worldPath = this.minecraft.getSingleplayerServer().getWorldPath(LevelResource.ROOT);
                            Util.getPlatform().openFile(worldPath.toFile());
                        }
                ));
            }
            if (ModHelper.options().accessibility.eChestButton.qoqMenu() && this.minecraft.player != null) {
                this.viewEnderChestButton = this.addRenderableWidget(ButtonHelper.createSpriteIconButton(
                        ofQoQ(ENDER_CHEST),
                        0,
                        0,
                        (button) -> {
                            this.minecraft.setScreen(new EnderChestPreviewScreen());
                        }
                ));
            }
        }

        this.wikiButton = this.addRenderableWidget(ButtonHelper.createSpriteIconButton(
                ofQoQ(WIKI_TEXTURE),
                0,
                0,
                ConfirmLinkScreen.confirmLink(this, WIKI_LINK, false)
        ));

        this.discordButton = this.addRenderableWidget(ButtonHelper.createSpriteIconButton(
                ofQoQ(DISCORD_TEXTURE),
                0,
                0,
                ConfirmLinkScreen.confirmLink(this, DISCORD_LINK, false)
        ));

        this.youtubeButton = this.addRenderableWidget(createYouTubeButton(this, this.youtubeLink()));
    }

    /**
     * Renders the tooltip for the done button.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        this.positionFooterButtons();
        super.render(graphics, mouseX, mouseY, deltaTicks);
        if (this.doneButton != null && this.doneButton.isMouseOver(mouseX, mouseY)) {
            String tooltip = Screen.hasShiftDown() ? "qualityofqueso.gui.open_config_directory" : "qualityofqueso.gui.open_config_directory.help";
            drawTooltip(Component.translatable(tooltip), graphics, this.font, mouseX, mouseY);
        }

        int textWidth = this.width - 20;
        int textHeight = this.height - 21;
        int imageWidth = this.width - (MultiLoader.getPlatform().getVersionType() == VersionType.RELEASE ? 50 : 53);
        int imageHeight = this.height - 26;
        graphics.drawCenteredString(this.font, VERSION, textWidth, textHeight, CommonColors.WHITE);
        graphics.blit(ofQoQ("textures/gui/sprites/" + CHEESE_WHEEL_TEXTURE + ".png"), imageWidth, imageHeight, 0.0F, 0.0F, 18, 18, 18, 18);

        if (buttonActive(this.screenshotsButton)) {
            if (ModHelper.options().accessibility.tooltips.on() && this.screenshotsButton.isMouseOver(mouseX, mouseY)) {
                drawTooltip(Component.translatable("qualityofqueso.gui.open_screenshots_folder"), graphics, this.font, mouseX, mouseY);
            }
        }
        if (buttonActive(this.worldDirectoryButton)) {
            if (ModHelper.options().accessibility.tooltips.on() && this.worldDirectoryButton.isMouseOver(mouseX, mouseY)) {
                drawTooltip(Component.translatable("qualityofqueso.gui.open_world_folder"), graphics, this.font, mouseX, mouseY);
            }
        }
        if (this.viewEnderChestButton != null) {
            this.viewEnderChestButton.active = modEnabled(this.minecraft);
            if (ModHelper.options().accessibility.tooltips.on() && this.viewEnderChestButton.isMouseOver(mouseX, mouseY)) {
                drawTooltip(Component.translatable("qualityofqueso.gui.view_ender_chest.tooltip"), graphics, this.font, mouseX, mouseY);
            }
        }

        if (buttonActive(this.wikiButton)) {
            if (this.wikiButton.isMouseOver(mouseX, mouseY)) {
                drawTooltip(Component.translatable("qualityofqueso.gui.learn_more"), graphics, this.font, mouseX, mouseY);
            }
        }
        if (buttonActive(this.discordButton)) {
            if (this.discordButton.isMouseOver(mouseX, mouseY)) {
                drawTooltip(Component.translatable("qualityofqueso.gui.discord"), graphics, this.font, mouseX, mouseY);
            }
        }
        if (buttonActive(this.youtubeButton)) {
            if (this.youtubeButton.isMouseOver(mouseX, mouseY)) {
                drawTooltip(this.getYouTubeVideoTooltip(), graphics, this.font, mouseX, mouseY);
            }
        }

        this.activateButtons();

        if (buttonActive(this.youtubeButton)) {
            this.blitYouTubeSprite(graphics);
        }
    }

    /**
     * Determines if buttons can be active.
     */
    protected void activateButtons() {
    }

    /**
     * Positions the footer buttons at the correct position.
     */
    private void positionFooterButtons() {
        if (this.doneButton == null) {
            return;
        }

        int leftIndex = 0;
        if (buttonActive(this.screenshotsButton)) {
            this.screenshotsButton.setPosition(getLeftButtonPosition(this.width, leftIndex), this.doneButton.getY());
            leftIndex++;
        }
        if (buttonActive(this.worldDirectoryButton)) {
            this.worldDirectoryButton.setPosition(getLeftButtonPosition(this.width, leftIndex), this.doneButton.getY());
            leftIndex++;
        }
        if (this.viewEnderChestButton != null) {
            this.viewEnderChestButton.setPosition(getLeftButtonPosition(this.width, leftIndex), this.doneButton.getY());
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
    }

    /**
     * @return the showcase video link to use.
     */
    protected String youtubeLink() {
        return SHOWCASE_VIDEO_LINK;
    }

    /**
     * @return the relevant button tooltip.
     */
    protected Component getYouTubeVideoTooltip() {
        return Component.translatable("qualityofqueso.gui.showcase.main.tooltip");
    }

    /**
     * The additional YouTube sprite to render over top of the YouTube button.
     */
    protected void blitYouTubeSprite(GuiGraphics graphics) {
    }

    /**
     * @return if all options in the {@link AbstractModOptionsScreen#options()} method should be added by default when calling {@code super.init().}
     */
    protected boolean addOptionsByDefault() {
        return true;
    }

    /**
     * Required method.
     */
    @Override
    protected void addOptions() {
    }
}
