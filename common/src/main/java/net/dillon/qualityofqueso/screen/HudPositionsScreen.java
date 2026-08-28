package net.dillon.qualityofqueso.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.dillonlib.util.Texts;
import net.dillon.qualityofqueso.util.ListOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

import static net.dillon.dillonlib.task.ClientTasks.*;
import static net.dillon.qualityofqueso.helper.GuiHelper.getArmorHotbarTexture;
import static net.dillon.qualityofqueso.helper.ModConstants.DISABLED_TEXTURE;
import static net.dillon.qualityofqueso.helper.ModConstants.ENABLED_TEXTURE;
import static net.dillon.qualityofqueso.helper.ModHelper.qoqIdentifier;
import static net.dillon.qualityofqueso.helper.ModHelper.saveAndApplyConfigs;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.updateClient;

/**
 * A screen used to configure the position of elements.
 */
public class HudPositionsScreen extends Screen {
    private AbstractWidget armorStatusXPosition, armorStatusYPosition, itemCounterXPosition, itemCounterYPosition, otherElementsY, visualClockX, visualClockY;
    private Button moveItemCounterOver;
    private final Screen parent;

    public HudPositionsScreen(Screen parent) {
        super(Component.translatable("qualityofqueso.gui.title.hud_positions"));
        this.parent = parent;
    }

    @Override
    public void onClose() {
        saveAndApplyConfigs(this.minecraft);
        openScreen(this.parent);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT && this.doInit()) {
            saveAndApplyConfigs(this.minecraft);
            this.init();
        }
        return super.mouseClicked(event, doubleClick);
    }

    /**
     * @return if init method should be called.
     */
    private boolean doInit() {
        if (this.armorStatusXPosition.isHovered()) {
            client().hud().armorStatusPosition[0] = 0;
            return true;
        } else if (this.armorStatusYPosition.isHovered()) {
            client().hud().armorStatusPosition[1] = 0;
            return true;
        } else if (this.itemCounterXPosition.isHovered()) {
            client().itemCounter().itemCounterPosition[0] = 0;
            return true;
        } else if (this.itemCounterYPosition.isHovered()) {
            client().itemCounter().itemCounterPosition[1] = 0;
            return true;
        } else if (this.otherElementsY.isHovered()) {
            client().hud().otherElementsY = 0;
            return true;
        } else if (this.visualClockX.isHovered()) {
            client().visualTime().visualClockPosition[0] = 0;
            return true;
        } else if (this.visualClockY.isHovered()) {
            client().visualTime().visualClockPosition[1] = 0;
            return true;
        }

        return false;
    }

    @Override
    protected void init() {
        this.clearWidgets();

        this.armorStatusXPosition = this.addRenderableWidget(ListOptions.armorStatusXPosition().createButton(Minecraft.getInstance().options, this.width / 2 - 100, this.height / 2 - 64, 200));
        this.armorStatusYPosition = this.addRenderableWidget(ListOptions.armorStatusYPosition().createButton(Minecraft.getInstance().options, this.armorStatusXPosition.getX(), this.armorStatusXPosition.getY() + 24, 200));

        this.itemCounterXPosition = this.addRenderableWidget(ListOptions.itemCounterXPosition().createButton(Minecraft.getInstance().options, this.armorStatusXPosition.getX(), this.armorStatusYPosition.getY() + 64, 200));
        this.itemCounterYPosition = this.addRenderableWidget(ListOptions.itemCounterYPosition().createButton(Minecraft.getInstance().options, this.armorStatusXPosition.getX(), this.itemCounterXPosition.getY() + 24, 200));
        this.moveItemCounterOver = this.addRenderableWidget(Button.builder(Texts.BLANK, button -> {
            updateClient(client -> {
                client.itemCounter().moveItemCounterOver = !client.itemCounter().moveItemCounterOver;
            });
        }).tooltip(
                Tooltip.create(Component.translatable("qualityofqueso.options.move_item_counter_over.tooltip"))
        ).bounds(this.itemCounterXPosition.getX() - 24, this.itemCounterXPosition.getY(), 20, 20).build());

        AbstractWidget reset = this.addRenderableWidget(Button.builder(Component.translatable("qualityofqueso.gui.reset"), button -> {
            updateClient(client -> {
                client.hud().armorStatusPosition[0] = 0;
                client.hud().armorStatusPosition[1] = 0;
                client.itemCounter().itemCounterPosition[0] = 0;
                client.itemCounter().itemCounterPosition[1] = 0;
                client.itemCounter().moveItemCounterOver = true;
                client.hud().otherElementsY = 0;
                client.visualTime().visualClockPosition[0] = 0;
                client.visualTime().visualClockPosition[1] = 0;
            });
            this.init();
        }).bounds(this.itemCounterXPosition.getX() + 50, this.itemCounterYPosition.getY() + 28, 100, 20).build());

        this.otherElementsY = this.addRenderableWidget(ListOptions.otherElementsYPosition().createButton(Minecraft.getInstance().options, reset.getX() - 132, reset.getY(), 125));
        this.visualClockX = this.addRenderableWidget(ListOptions.visualClockXPosition().createButton(Minecraft.getInstance().options, reset.getX() + 106, reset.getY(), 125));
        this.visualClockY = this.addRenderableWidget(ListOptions.visualClockYPosition().createButton(Minecraft.getInstance().options, this.visualClockX.getX(), this.visualClockX.getY() + 24, 125));

        this.addRenderableWidget(Button.builder(Component.translatable("qualityofqueso.gui.save_changes"), button -> {
            this.onClose();
        }).bounds(reset.getX(), reset.getY() + 24, 100, 20).build());

        boolean armorStatusEnabled = !client().hud().armorStatus.off();
        this.armorStatusXPosition.active = armorStatusEnabled;
        this.armorStatusYPosition.active = armorStatusEnabled;

        boolean itemCounterEnabled = client().itemCounter().itemCounter.enabled();
        this.itemCounterXPosition.active = itemCounterEnabled;
        this.itemCounterYPosition.active = itemCounterEnabled;
        this.moveItemCounterOver.active = itemCounterEnabled;

        boolean visualClockEnabled = client().visualTime().displayVisualClock;
        this.visualClockX.active = visualClockEnabled;
        this.visualClockY.active = visualClockEnabled;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        if (this.minecraft.level == null) {
            this.extractPanorama(graphics, deltaTicks);
        }
        this.extractMenuBackground(graphics);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, deltaTicks);

        graphics.centeredText(this.font, this.title, this.width / 2, 13, CommonColors.WHITE);
        graphics.text(this.font, Component.translatable("qualityofqueso.gui.hud_positions.warning"), this.width / 2 - 130, 44, CommonColors.WHITE);
        drawSmallSprite(graphics, client().itemCounter().moveItemCounterOver ? qoqIdentifier(ENABLED_TEXTURE) : qoqIdentifier(DISABLED_TEXTURE), this.moveItemCounterOver);

        drawSprite(graphics, getArmorHotbarTexture(), this.armorStatusXPosition.getX() + 60, this.armorStatusXPosition.getY() - 28, 82, 22);
        drawSprite(graphics, qoqIdentifier("hud/hud_positions_screen/item_counter"), this.armorStatusXPosition.getX() + 74, this.itemCounterXPosition.getY() - 32, 58, 30);
    }
}