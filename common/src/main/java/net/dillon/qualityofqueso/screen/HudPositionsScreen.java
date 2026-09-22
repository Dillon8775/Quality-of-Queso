package net.dillon.qualityofqueso.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.dillonlib.screen.BasicDillonLibScreen;
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
public class HudPositionsScreen extends BasicDillonLibScreen {
    private AbstractWidget xPosArmorStatus, yPosArmorStatus, xPosItemCounter, yPosItemCounter, moveItemCounterOver, yPosOther, xPosVisualClock, yPosVisualClock;
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
        if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT && this.resetSpecifiedValue()) {
            saveAndApplyConfigs(this.minecraft);
            this.widgets();
        }
        return super.mouseClicked(event, doubleClick);
    }

    /**
     * @return if the reset value for a certain option should be called.
     */
    private boolean resetSpecifiedValue() {
        if (this.xPosArmorStatus.isHovered()) {
            client().hud().armorStatusPosition[0] = 0;
            return true;
        } else if (this.yPosArmorStatus.isHovered()) {
            client().hud().armorStatusPosition[1] = 0;
            return true;
        } else if (this.xPosItemCounter.isHovered()) {
            client().itemCounter().itemCounterPosition[0] = 0;
            return true;
        } else if (this.yPosItemCounter.isHovered()) {
            client().itemCounter().itemCounterPosition[1] = 0;
            return true;
        } else if (this.yPosOther.isHovered()) {
            client().hud().otherElementsY = 0;
            return true;
        } else if (this.xPosVisualClock.isHovered()) {
            client().visualTime().visualClockPosition[0] = 0;
            return true;
        } else if (this.yPosVisualClock.isHovered()) {
            client().visualTime().visualClockPosition[1] = 0;
            return true;
        }

        return false;
    }

    /**
     * @return if the user is able to change the position of the armor status.
     */
    private boolean isArmorStatusEnabled() {
        return !client().hud().armorStatus.off();
    }

    /**
     * @return if the user is able to change the position of the item counter.
     */
    private boolean isItemCounterEnabled() {
        return client().itemCounter().itemCounter.enabled();
    }

    /**
     * @return if the user is able to change the position of the visual clock.
     */
    private boolean isVisualClockEnabled() {
        return client().visualTime().displayVisualClock;
    }

    /**
     * @return the armor status x-pos config button.
     */
    private AbstractWidget xPosArmorStatusButton() {
        return this.createWidget(
                ListOptions.armorStatusXPosition().createButton(
                        Minecraft.getInstance().options,
                        builder().captureWidth(),
                        builder().captureHeight(),
                        200
                ),
                this::isArmorStatusEnabled
        );
    }

    /**
     * @return the armor status y-pos config button.
     */
    private AbstractWidget yPosArmorStatusButton() {
        return this.createWidget(
                ListOptions.armorStatusYPosition().createButton(
                        Minecraft.getInstance().options,
                        builder().captureWidth(),
                        builder().captureHeight(),
                        200
                ),
                this::isArmorStatusEnabled
        );
    }

    /**
     * @return the item counter x-pos config button.
     */
    private AbstractWidget xPosItemCounterButton() {
        return this.createWidget(
                ListOptions.itemCounterXPosition().createButton(
                        Minecraft.getInstance().options,
                        builder().captureWidth(),
                        builder().captureHeight(),
                        200
                ),
                this::isItemCounterEnabled
        );
    }

    /**
     * @return the item counter y-pos config button.
     */
    private AbstractWidget yPosItemCounterButton() {
        return this.createWidget(
                ListOptions.itemCounterYPosition().createButton(
                        Minecraft.getInstance().options,
                        builder().captureWidth(),
                        builder().captureHeight(),
                        200
                ),
                this::isItemCounterEnabled
        );
    }

    /**
     * @return the reset all positions button.
     */
    private AbstractWidget resetButton() {
        return Button.builder(Component.translatable("qualityofqueso.gui.reset"), button -> {
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
            this.widgets();
        }).bounds(builder().captureWidth(), builder().captureHeight(), 100, 20).build();
    }

    @Override
    public void widgets() {
        this.clearWidgets();

        builder().heightCenter().apply();
        builder().widthCenter().apply();

        builder().widthLeft(100).apply();
        builder().heightUp(64).apply();

        this.xPosArmorStatus = this.addRenderableWidget(xPosArmorStatusButton());

        builder().heightDown().apply();

        this.yPosArmorStatus = this.addRenderableWidget(yPosArmorStatusButton());

        builder().heightDown(64).apply();

        this.xPosItemCounter = this.addRenderableWidget(xPosItemCounterButton());

        builder().heightDown().apply();

        this.yPosItemCounter = this.addRenderableWidget(yPosItemCounterButton());

        this.moveItemCounterOver = this.addRenderableWidget(
                this.createWidget(
                        Button.builder(Texts.BLANK, button -> {
                            updateClient(client -> {
                                client.itemCounter().moveItemCounterOver = !client.itemCounter().moveItemCounterOver;
                            });
                        }).tooltip(
                                Tooltip.create(Component.translatable("qualityofqueso.options.move_item_counter_over.tooltip"))
                        ).bounds(builder().widthLeft().pop(), builder().heightUp().pop(), 20, 20).build(),
                        this::isItemCounterEnabled
                )
        );

        builder().widthRight(50).apply();
        builder().heightDown(28).apply();

        this.addRenderableWidget(resetButton());

        this.yPosOther = this.addRenderableWidget(
                ListOptions.otherElementsYPosition().createButton(
                        Minecraft.getInstance().options,
                        builder().widthLeft(132).pop(),
                        builder().captureHeight(),
                        125
                )
        );

        this.xPosVisualClock = this.addRenderableWidget(
                this.createWidget(
                        ListOptions.visualClockXPosition().createButton(
                                Minecraft.getInstance().options,
                                builder().widthRight(106).pop(),
                                builder().captureHeight(),
                                125
                        ),
                        this::isVisualClockEnabled
                )
        );

        this.yPosVisualClock = this.addRenderableWidget(
                this.createWidget(
                        ListOptions.visualClockYPosition().createButton(
                                Minecraft.getInstance().options,
                                builder().widthRight(106).pop(),
                                builder().heightDown().pop(),
                                125
                        ),
                        this::isVisualClockEnabled
                )
        );

        builder().heightDown().apply();

        this.addRenderableWidget(Button.builder(Component.translatable("qualityofqueso.gui.save_changes"), button -> {
            this.onClose();
        }).bounds(builder().captureWidth(), builder().captureHeight(), 100, 20).build());
    }

    @Override
    protected void drawGraphics(GuiGraphicsExtractor graphics) {
        builder().renderHeightTop().apply();
        builder().renderWidthCenter().apply();

        builder().renderHeightDown(13).apply();

        builder().textCenterAndHeightDown(graphics, this.title, 31).apply();
        builder().textCenterAndHeightDown(graphics, Component.translatable("qualityofqueso.gui.hud_positions.warning")).apply();

        if (this.moveItemCounterOver instanceof Button button) {
            drawSmallSprite(graphics, client().itemCounter().moveItemCounterOver ? qoqIdentifier(ENABLED_TEXTURE) : qoqIdentifier(DISABLED_TEXTURE), button);
        }

        builder().renderWidth(xPosArmorStatus).apply();

        drawSprite(
                graphics,
                getArmorHotbarTexture(),
                builder().captureRenderWidth() + 60,
                builder().renderHeight(xPosArmorStatus).pop() - 28,
                82,
                22
        );

        drawSprite(
                graphics,
                qoqIdentifier("hud/hud_positions_screen/item_counter"),
                builder().captureRenderWidth() + 74,
                builder().renderHeight(xPosItemCounter).pop() - 32,
                58,
                30
        );
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        if (this.minecraft.level == null) {
            this.extractPanorama(graphics, deltaTicks);
        }

        this.extractMenuBackground(graphics);
    }
}