package net.dillon.qualityofqueso.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dillon.qualityofqueso.helper.ButtonHelper;
import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.util.ListOptions;
import net.dillon.qualityofqueso.util.ModTexts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

import static net.dillon.qualityofqueso.helper.GuiHelper.getArmorHotbarTexture;
import static net.dillon.qualityofqueso.helper.ModHelper.*;
import static net.dillon.qualityofqueso.util.ModConstants.DISABLED_TEXTURE;
import static net.dillon.qualityofqueso.util.ModConstants.ENABLED_TEXTURE;

/**
 * A screen used to configure the position of elements.
 */
public class HudPositionsScreen extends Screen {
    private AbstractWidget armorStatusXPosition, armorStatusYPosition, itemCounterXPosition, itemCounterYPosition, otherElementsY;
    private Button moveItemCounterOver;
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
    public boolean mouseClicked(double mouseX, double mouseY, int bl) {
        if (bl == 1) {
            boolean doInit = false;
            if (this.armorStatusXPosition.isMouseOver(mouseX, mouseY)) {
                clientOptionsInstance().getHudOptions().armorStatusPosition[0] = 0;
                doInit = true;
            } else if (this.armorStatusYPosition.isMouseOver(mouseX, mouseY)) {
                clientOptionsInstance().getHudOptions().armorStatusPosition[1] = 0;
                doInit = true;
            } else if (this.itemCounterXPosition.isMouseOver(mouseX, mouseY)) {
                clientOptionsInstance().getItemCounterOptions().itemCounterPosition[0] = 0;
                doInit = true;
            } else if (this.itemCounterYPosition.isMouseOver(mouseX, mouseY)) {
                clientOptionsInstance().getItemCounterOptions().itemCounterPosition[1] = 0;
                doInit = true;
            } else if (this.otherElementsY.isMouseOver(mouseX, mouseY)) {
                clientOptionsInstance().getHudOptions().otherElementsY = 0;
                doInit = true;
            }

            if (doInit) {
                saveAndApplyConfigs(this.minecraft);
                this.init();
            }
        }
        return super.mouseClicked(mouseX, mouseY, bl);
    }

    @Override
    protected void init() {
        this.clearWidgets();

        this.armorStatusXPosition = this.addRenderableWidget(ListOptions.armorStatusXPosition().createButton(Minecraft.getInstance().options, this.width / 2 - 100, this.height / 2 - 64, 200));
        this.armorStatusYPosition = this.addRenderableWidget(ListOptions.armorStatusYPosition().createButton(Minecraft.getInstance().options, this.armorStatusXPosition.getX(), this.armorStatusXPosition.getY() + 24, 200));

        this.itemCounterXPosition = this.addRenderableWidget(ListOptions.itemCounterXPosition().createButton(Minecraft.getInstance().options, this.armorStatusXPosition.getX(), this.armorStatusYPosition.getY() + 64, 200));
        this.itemCounterYPosition = this.addRenderableWidget(ListOptions.itemCounterYPosition().createButton(Minecraft.getInstance().options, this.armorStatusXPosition.getX(), this.itemCounterXPosition.getY() + 24, 200));
        this.moveItemCounterOver = this.addRenderableWidget(Button.builder(ModTexts.BLANK, button -> {
            ModClientOptions.INSTANCE.update(options -> {
                options.getItemCounterOptions().moveItemCounterOver = !options.getItemCounterOptions().moveItemCounterOver;
            });
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.options.move_item_counter_over.tooltip"))
        ).bounds(this.itemCounterXPosition.getX() - 24, this.itemCounterXPosition.getY(), 20, 20).build());

        AbstractWidget reset = this.addRenderableWidget(Button.builder(Component.translatable("qualityofqueso.gui.reset"), button -> {
            ModClientOptions.INSTANCE.update(options -> {
                options.getHudOptions().armorStatusPosition[0] = 0;
                options.getHudOptions().armorStatusPosition[1] = 0;
                options.getItemCounterOptions().itemCounterPosition[0] = 0;
                options.getItemCounterOptions().itemCounterPosition[1] = 0;
                options.getItemCounterOptions().moveItemCounterOver = true;
                options.getHudOptions().otherElementsY = 0;
            });
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
        this.moveItemCounterOver.active = itemCounterEnabled;
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

        ButtonHelper.drawTexture(graphics, clientOptionsInstance().getItemCounterOptions().moveItemCounterOver ? ENABLED_TEXTURE : DISABLED_TEXTURE, this.moveItemCounterOver);

        RenderSystem.enableBlend();
        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, -90.0F);
        graphics.blitSprite(getArmorHotbarTexture(), this.armorStatusXPosition.getX() + 60, this.armorStatusXPosition.getY() - 28, 82, 22);
        graphics.pose().popPose();
        RenderSystem.disableBlend();

        graphics.blitSprite(ofQoQ("hud/item_counter"), this.armorStatusXPosition.getX() + 74, this.itemCounterXPosition.getY() - 32, 58, 30);
    }
}