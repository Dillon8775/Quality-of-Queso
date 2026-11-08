package net.dillon.qualityofqueso.screen;

import com.mojang.blaze3d.platform.InputConstants;
import net.dillon.dillonlib.client.component.DoneButtonBuilder;
import net.dillon.dillonlib.client.component.GridBuilder;
import net.dillon.dillonlib.client.component.TextureBuilder;
import net.dillon.dillonlib.client.screen.DillonLibScreen;
import net.dillon.dillonlib.widespread.DillonsTexts;
import net.dillon.qualityofqueso.util.ListOptions;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import static net.dillon.dillonlib.client.component.GridBuilder.createColumn;
import static net.dillon.dillonlib.client.component.GridBuilder.createRow;
import static net.dillon.dillonlib.task.ClientTasks.isCurrentLevelNull;
import static net.dillon.dillonlib.task.ClientTasks.openScreen;
import static net.dillon.dillonlib.widespread.DillonsWidgets.*;
import static net.dillon.qualityofqueso.helper.GuiHelper.drawVisualTimeClock;
import static net.dillon.qualityofqueso.helper.GuiHelper.getArmorHotbarTexture;
import static net.dillon.qualityofqueso.helper.ModHelper.qoqIdentifier;
import static net.dillon.qualityofqueso.helper.ModHelper.saveAndApplyChanges;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.option.OptionInstances.updateClient;

/**
 * A screen used to configure the position of elements.
 */
public class HudPositionsScreen extends DillonLibScreen {
    private AbstractWidget xPosArmorStatus, yPosArmorStatus, xPosItemCounter, yPosItemCounter, moveItemCounterOver, yPosOther, xPosVisualClock, yPosVisualClock;
    private final Screen parent;

    public HudPositionsScreen(Screen parent) {
        super(Component.translatable("qualityofqueso.gui.title.hud_positions"));
        this.parent = parent;
    }

    @Override
    public void onClose() {
        saveAndApplyChanges();
        openScreen(parent);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT && this.resetSpecifiedValue()) {
            saveAndApplyChanges();
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
        return client().visualTime().overrideClientTime && client().visualTime().displayVisualClock;
    }

    /**
     * @return the armor status x-pos config button.
     */
    private AbstractWidget xPosArmorStatusButton() {
        return createWidget(
                createOption(
                        ListOptions.armorStatusXPosition(),
                        200
                ),
                this::isArmorStatusEnabled
        );
    }

    /**
     * @return the armor status y-pos config button.
     */
    private AbstractWidget yPosArmorStatusButton() {
        return createWidget(
                createOption(
                        ListOptions.armorStatusYPosition(),
                        200
                ),
                this::isArmorStatusEnabled
        );
    }

    /**
     * @return the item counter x-pos config button.
     */
    private AbstractWidget xPosItemCounterButton() {
        return createWidget(
                createOption(
                        ListOptions.itemCounterXPosition(),
                        175
                ),
                this::isItemCounterEnabled
        );
    }

    /**
     * @return the item counter y-pos config button.
     */
    private AbstractWidget yPosItemCounterButton() {
        return createWidget(
                createOption(
                        ListOptions.itemCounterYPosition(),
                        200
                ),
                this::isItemCounterEnabled
        );
    }

    /**
     * @return the move item counter over config button.
     */
    private AbstractWidget moveItemCounterOverButton() {
        return createWidget(
                Checkbox.builder(DillonsTexts.BLANK, getFont())
                        .onValueChange((checkbox, value) -> updateClient(client -> {
                            client.itemCounter().moveItemCounterOver = value;
                        }))
                        .maxWidth(20)
                        .selected(client().itemCounter().moveItemCounterOver)
                        .build(),
                this::isItemCounterEnabled,
                isActive -> Component.translatable("qualityofqueso.options.move_item_counter_over.tooltip")
        );
    }

    /**
     * @return the y-pos for other elements config button.
     */
    private AbstractWidget yPosOtherButton() {
        return createOption(
                ListOptions.otherElementsYPosition(),
                150
        );
    }

    /**
     * @return the x-pos visual clock config button.
     */
    private AbstractWidget xPosVisualClockButton() {
        return createOption(
                ListOptions.visualClockXPosition(),
                200
        );
    }

    /**
     * @return the y-pos visual clock config button.
     */
    private AbstractWidget yPosVisualClockButton() {
        return createOption(
                ListOptions.visualClockYPosition(),
                200
        );
    }

    /**
     * @return the button to save changes.
     */
    private AbstractWidget saveChangesButton() {
        return DoneButtonBuilder.builder(this)
                .text(Component.translatable("qualityofqueso.gui.save_changes"))
                .width(100)
                .build();
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
        }).width(100).build();
    }

    @Override
    public void widgets() {
        this.clearWidgets();

        addTitleToHeader(this);

        GridBuilder builder = GridBuilder.create(this)
                .setRowSpacing(8)
                .setColumnSpacing(24);

        xPosArmorStatus = xPosArmorStatusButton();
        yPosArmorStatus = yPosArmorStatusButton();
        xPosItemCounter = xPosItemCounterButton();
        yPosItemCounter = yPosItemCounterButton();
        moveItemCounterOver = moveItemCounterOverButton();
        yPosOther = yPosOtherButton();
        xPosVisualClock = xPosVisualClockButton();
        yPosVisualClock = yPosVisualClockButton();

        LinearLayout controls = createColumn();

        controls.addChild(xPosArmorStatus);
        controls.addChild(
                yPosArmorStatus,
                builder.getGrid().newCellSettings().paddingBottom(20)
        );

        LinearLayout itemCounterXControls = createRow();
        itemCounterXControls.addChild(xPosItemCounter);
        itemCounterXControls.addChild(moveItemCounterOver);
        controls.addChild(itemCounterXControls);
        controls.addChild(
                yPosItemCounter,
                builder.getGrid().newCellSettings().paddingBottom(20)
        );

        controls.addChild(xPosVisualClock);
        controls.addChild(yPosVisualClock);

        builder.getCurrentRow().addChild(controls);

        GridBuilder offsetBuilder = GridBuilder.create(this);

        LinearLayout additionalControls = createRow();
        additionalControls.addChild(yPosOther);

        LinearLayout writeControls = createRow();
        writeControls.addChild(resetButton());
        writeControls.addChild(saveChangesButton());

        LinearLayout rightColumn = createColumn();
        rightColumn.defaultCellSetting().alignHorizontallyCenter();

        MultiLineTextWidget warningLabel = createMultiLineText(Component.translatable("qualityofqueso.gui.hud_positions.warning"));
        rightColumn.addChild(
                warningLabel,
                builder.getGrid().newCellSettings().paddingBottom(32)
        );

        rightColumn.addChild(additionalControls);
        rightColumn.addChild(writeControls);
        offsetBuilder.getCurrentRow().addChild(rightColumn);

        builder.setHorizontalAlignment(0.2F).buildAndAlign();
        offsetBuilder.setHorizontalAlignment(0.8F).buildAndAlign();
    }

    @Override
    protected void drawGraphics(GuiGraphicsExtractor graphics) {
        int xAlignment = 210;
        int yAlignment = 12;

        TextureBuilder.create(
                graphics,
                getArmorHotbarTexture(),
                xPosArmorStatus.getX() + xAlignment,
                xPosArmorStatus.getY() + yAlignment,
                82,
                22
        ).buildAndBlit(true);

        TextureBuilder.create(
                graphics,
                qoqIdentifier("config_images/item_counter"),
                xPosItemCounter.getX() + xAlignment - 10,
                xPosItemCounter.getY() + yAlignment,
                58,
                30
        ).buildAndBlit(true);

        drawVisualTimeClock(
                graphics,
                xPosVisualClock.getX() + xAlignment,
                xPosVisualClock.getY() + yAlignment,
                24,
                true
        );
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        if (isCurrentLevelNull()) {
            this.extractPanorama(graphics, deltaTicks);
        }

        this.extractMenuBackground(graphics);
    }
}