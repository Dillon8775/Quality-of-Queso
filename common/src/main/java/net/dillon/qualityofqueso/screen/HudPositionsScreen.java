package net.dillon.qualityofqueso.screen;

import net.dillon.qualityofqueso.util.ListOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

import static net.dillon.qualityofqueso.helper.GuiHelper.getArmorHotbarTexture;
import static net.dillon.qualityofqueso.helper.ModHelper.*;

/**
 * A screen used to configure the position of elements.
 */
public class HudPositionsScreen extends Screen {
    private AbstractWidget armorStatusXPosition, armorStatusYPosition, itemCounterXPosition, itemCounterYPosition, otherElementsY;
    private final Screen parent;

    public HudPositionsScreen(Screen parent) {
        super(Component.translatable("qualityofqueso.gui.title.hud_positions"));
        this.parent = parent;
    }

    @Override
    public void onClose() {
        saveAndApplyConfigs(this.minecraft);
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 1) {
            boolean doInit = false;
            if (this.armorStatusXPosition.isHovered()) {
                clientOptionsInstance().getHudOptions().armorStatusPosition[0] = 0;
                doInit = true;
            } else if (this.armorStatusYPosition.isHovered()) {
                clientOptionsInstance().getHudOptions().armorStatusPosition[1] = 0;
                doInit = true;
            } else if (this.itemCounterXPosition.isHovered()) {
                clientOptionsInstance().getItemCounterOptions().itemCounterPosition[0] = 0;
                doInit = true;
            } else if (this.itemCounterYPosition.isHovered()) {
                clientOptionsInstance().getItemCounterOptions().itemCounterPosition[1] = 0;
                doInit = true;
            } else if (this.otherElementsY.isHovered()) {
                clientOptionsInstance().getHudOptions().otherElementsY = 0;
                doInit = true;
            }

            if (doInit) {
                saveAndApplyConfigs(this.minecraft);
                this.init();
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    protected void init() {
        this.clearWidgets();

        this.armorStatusXPosition = this.addRenderableWidget(ListOptions.armorStatusXPosition().createButton(Minecraft.getInstance().options, this.width / 2 - 100, this.height / 2 - 64, 200));
        this.armorStatusYPosition = this.addRenderableWidget(ListOptions.armorStatusYPosition().createButton(Minecraft.getInstance().options, this.armorStatusXPosition.getX(), this.armorStatusXPosition.getY() + 24, 200));

        this.itemCounterXPosition = this.addRenderableWidget(ListOptions.itemCounterXPosition().createButton(Minecraft.getInstance().options, this.armorStatusXPosition.getX(), this.armorStatusYPosition.getY() + 64, 200));
        this.itemCounterYPosition = this.addRenderableWidget(ListOptions.itemCounterYPosition().createButton(Minecraft.getInstance().options, this.armorStatusXPosition.getX(), this.itemCounterXPosition.getY() + 24, 200));

        AbstractWidget reset = this.addRenderableWidget(Button.builder(Component.translatable("qualityofqueso.gui.reset"), button -> {
            clientOptionsInstance().getHudOptions().armorStatusPosition[0] = 0;
            clientOptionsInstance().getHudOptions().armorStatusPosition[1] = 0;
            clientOptionsInstance().getItemCounterOptions().itemCounterPosition[0] = 0;
            clientOptionsInstance().getItemCounterOptions().itemCounterPosition[1] = 0;
            clientOptionsInstance().getHudOptions().otherElementsY = 0;
            this.init();
        }).bounds(this.itemCounterXPosition.getX() + 50, this.itemCounterYPosition.getY() + 28, 100, 20).build());

        this.otherElementsY = this.addRenderableWidget(ListOptions.otherElementsYPosition().createButton(Minecraft.getInstance().options, reset.getX() + 112, reset.getY(), 125));

        this.addRenderableWidget(Button.builder(Component.translatable("qualityofqueso.gui.save_changes"), button -> {
            this.onClose();
        }).bounds(reset.getX(), reset.getY() + 24, 100, 20).build());

        boolean armorStatusEnabled = !clientOptionsInstance().getHudOptions().armorStatus.off();
        this.armorStatusXPosition.active = armorStatusEnabled;
        this.armorStatusYPosition.active = armorStatusEnabled;

        boolean itemCounterEnabled = clientOptionsInstance().getItemCounterOptions().itemCounter.enabled();
        this.itemCounterXPosition.active = itemCounterEnabled;
        this.itemCounterYPosition.active = itemCounterEnabled;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        if (this.minecraft.level == null) {
            this.renderPanorama(graphics, deltaTicks);
        }
        this.renderMenuBackground(graphics);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        super.render(graphics, mouseX, mouseY, deltaTicks);

        graphics.drawCenteredString(this.font, this.title, this.width / 2, 13, CommonColors.WHITE);

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, getArmorHotbarTexture(), this.armorStatusXPosition.getX() + 60, this.armorStatusXPosition.getY() - 28, 82, 22);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ofQoQ("hud/item_counter"), this.armorStatusXPosition.getX() + 74, this.itemCounterXPosition.getY() - 32, 58, 30);
    }
}