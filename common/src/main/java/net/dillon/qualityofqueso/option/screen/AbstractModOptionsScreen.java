package net.dillon.qualityofqueso.option.screen;

import net.dillon.qualityofqueso.platform.MultiLoader;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.dillon.qualityofqueso.util.ModTexts;
import net.dillon.qualityofqueso.util.ModUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.nio.file.Path;

import static net.dillon.qualityofqueso.util.ButtonUtil.*;
import static net.dillon.qualityofqueso.util.ModUtil.*;

public abstract class AbstractModOptionsScreen extends OptionsSubScreen {
    private Button doneButton;
    private SpriteIconButton worldDirectoryButton;
    private SpriteIconButton screenshotsButton;

    public AbstractModOptionsScreen(Screen parent, Component title) {
        super(parent, Minecraft.getInstance().options, title);
    }

    /**
     * The list of {@link OptionInstance}s that should be added to the screen.
     */
    protected abstract OptionInstance<?>[] options();

    @Override
    protected void init() {
        super.init();
        if (this.addOptionsByDefault()) {
            this.list.addSmall(this.options());
        }
    }

    @Override
    protected void addFooter() {
        LinearLayout linearLayout = this.layout.addToFooter(LinearLayout.vertical()).spacing(8);
        linearLayout.defaultCellSetting().alignHorizontallyCenter();
        LinearLayout linearLayout2 = linearLayout.addChild(LinearLayout.horizontal().spacing(8));
        linearLayout2.addChild(
                Button.builder(Component.translatable("qualityofqueso.gui.learn_more"),
                        ConfirmLinkScreen.confirmLink(this, ModUtil.WIKI_LINK, false)).build());
        this.doneButton = linearLayout2.addChild(Button.builder(CommonComponents.GUI_DONE, button -> {
            if (Screen.hasShiftDown()) {
                Util.getPlatform().openFile(MultiLoader.getPlatform().getConfigDir().toFile());
            } else {
                this.onClose();
            }
        }).build());
    }

    @Override
    public void onClose() {
        saveAll(this.minecraft);
        ModUtil.debug("Saved changes.");
        if (this.minecraft.level != null) {
            sendClientOptionsToServer();
        }
        super.onClose();
    }

    /**
     * Renders the tooltip for the done button.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        super.render(graphics, mouseX, mouseY, deltaTicks);

        if (this.doneButton != null && this.doneButton.isHovered()) {
            String tooltip = Screen.hasShiftDown() ? "qualityofqueso.gui.open_config_directory" : "qualityofqueso.gui.open_config_directory.help";
            ButtonUtil.drawTooltip(Component.translatable(tooltip), graphics, this.font, mouseX, mouseY);
        }

        graphics.drawCenteredString(this.font, ModUtil.VERSION, this.width - 20, this.height - 21, CommonColors.WHITE);
        graphics.blit(ofQoQ("textures/gui/sprites/" + CHEESE_WHEEL_TEXTURE + ".png"), this.width - 50, this.height - 26, 0.0F, 0.0F, 18, 18, 18, 18);

        if (this.doneButton != null) {
            if (this.screenshotsButton == null) {
                this.screenshotsButton = this.addRenderableWidget(SpriteIconButton.builder(ModTexts.BLANK, (button) -> {
                    Util.getPlatform().openFile(new File(Minecraft.getInstance().gameDirectory, "screenshots"));
                }, false).width(20).sprite(ofQoQ(OPEN_SCREENSHOTS_DIRECTORY_TEXTURE), 16, 16).build());
            }
            if (this.worldDirectoryButton == null && this.minecraft.getSingleplayerServer() != null && this.minecraft.level != null) {
                this.worldDirectoryButton = this.addRenderableWidget(SpriteIconButton.builder(ModTexts.BLANK, (button) -> {
                    Path worldPath = this.minecraft.getSingleplayerServer().getWorldPath(LevelResource.ROOT);
                    Util.getPlatform().openFile(worldPath.toFile());
                }, false).width(20).sprite(ofQoQ(OPEN_WORLD_DIRECTORY_TEXTURE), 16, 16).build());
            }

            if (this.screenshotsButton != null) {
                setButtonPosition(this.screenshotsButton, this.width, this.doneButton.getY(), 0);
                if (this.screenshotsButton.isHovered()) {
                    ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.open_screenshots_folder"), graphics, this.font, mouseX, mouseY);
                }
            }
            if (this.worldDirectoryButton != null) {
                setButtonPosition(this.worldDirectoryButton, this.width, this.doneButton.getY(), 1);
                if (this.worldDirectoryButton.isHovered()) {
                    ButtonUtil.drawTooltip(Component.translatable("qualityofqueso.gui.open_world_directory"), graphics, this.font, mouseX, mouseY);
                }
            }
        }
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