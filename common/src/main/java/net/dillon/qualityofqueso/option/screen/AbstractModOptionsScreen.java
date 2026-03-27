package net.dillon.qualityofqueso.option.screen;

import net.dillon.qualityofqueso.platform.MultiLoader;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.dillon.qualityofqueso.util.ModTexts;
import net.dillon.qualityofqueso.util.ModUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.LevelResource;

import java.nio.file.Path;

import static net.dillon.qualityofqueso.util.ModUtil.*;

public abstract class AbstractModOptionsScreen extends OptionsSubScreen {
    private Button doneButton;
    private SpriteIconButton worldDirectoryButton;

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
            if (Minecraft.getInstance().hasShiftDown()) {
                Util.getPlatform().openFile(MultiLoader.PLATFORM.getConfigDir().toFile());
            } else {
                this.onClose();
            }
        }).build());
    }

    @Override
    public void onClose() {
        saveAll(this.minecraft);
        ModUtil.info("Saved changes.");
        if (this.minecraft.level != null) {
            sendClientOptionsToServer();
        }
        super.onClose();
    }

    /**
     * Renders the tooltip for the done button.
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        if (this.doneButton != null && this.doneButton.isHovered()) {
            String tooltip = Minecraft.getInstance().hasShiftDown() ? "qualityofqueso.gui.open_config_directory" : "qualityofqueso.gui.open_config_directory.help";
            ButtonUtil.drawTooltip(Component.translatable(tooltip), graphics, this.font, mouseX, mouseY);
        }

        if (this.doneButton != null) {
            graphics.centeredText(this.font, ModUtil.VERSION, this.width - 25, this.doneButton.getY() + 5, CommonColors.WHITE);
            graphics.blit(RenderPipelines.GUI_TEXTURED, ofQoQ("textures/gui/sprites/button/cheese_wheel.png"), this.width - 57, this.doneButton.getY(), 0.0F, 0.0F, 18, 18, 18, 18);
            if (this.worldDirectoryButton == null && this.minecraft.getSingleplayerServer() != null && this.minecraft.level != null) {
                this.worldDirectoryButton = this.addRenderableWidget(SpriteIconButton.builder(ModTexts.BLANK, (button) -> {
                    Path worldPath = this.minecraft.getSingleplayerServer().getWorldPath(LevelResource.ROOT);
                    Util.getPlatform().openFile(worldPath.toFile());
                }, false).width(20).sprite(ofQoQ("button/world_directory"), 16, 16).build());
            }
            if (this.worldDirectoryButton != null) {
                this.worldDirectoryButton.setPosition(this.width / 2 - 179, this.doneButton.getY());
            }
        }

        super.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
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