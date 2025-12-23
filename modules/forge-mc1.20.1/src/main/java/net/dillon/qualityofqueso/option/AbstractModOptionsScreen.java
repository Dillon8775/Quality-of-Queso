package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.main.QoQ;
import net.dillon.qualityofqueso.util.ModUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractModOptionsScreen extends OptionsSubScreen {
    protected OptionsList list;

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
        if (this.addList()) {
            this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
            if (this.addOptionsByDefault()) {
                this.list.addSmall(this.options());
            }
            this.addWidget(this.list);
        }

        int buttonWidth = 150;
        int buttonHeight = 20;
        int spacing = 8;

        int centerX = this.width / 2;
        int y = this.height - 29;

        this.addRenderableWidget(Button.builder(
                Component.translatable("qualityofqueso.gui.learn_more"), ConfirmLinkScreen.confirmLink("https://modrinth.com/mod/quality-of-queso", this, true)
        ).bounds(centerX - buttonWidth - spacing / 2, y, buttonWidth, buttonHeight).build());

        this.addRenderableWidget(Button.builder(
                CommonComponents.GUI_DONE,
                button -> this.onClose()
        ).bounds(centerX + spacing / 2, y, buttonWidth, buttonHeight).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        this.renderBackground(graphics);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 16777215);
        if (this.list != null) {
            this.list.render(graphics, mouseX, mouseY, deltaTicks);
        }
        super.render(graphics, mouseX, mouseY, deltaTicks);
    }

    @Override
    public void onClose() {
        QoQ.saveAll();
        ModUtil.info("Saved changes.");
        super.onClose();
    }

    /**
     * @return if all options in the {@link AbstractModOptionsScreen#options()} method should be added by default when calling {@code super.init().}
     */
    protected boolean addOptionsByDefault() {
        return true;
    }

    /**
     * @return if the screen should have the list rendered.
     */
    protected boolean addList() {
        return true;
    }
}