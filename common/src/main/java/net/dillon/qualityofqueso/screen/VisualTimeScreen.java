package net.dillon.qualityofqueso.screen;

import net.dillon.dillonlib.client.component.GridBuilder;
import net.dillon.dillonlib.client.component.TextureBuilder;
import net.dillon.dillonlib.client.screen.DillonLibScreen;
import net.dillon.dillonlib.widespread.DillonsTexts;
import net.dillon.qualityofqueso.util.ListOptions;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

import static net.dillon.dillonlib.client.component.GridBuilder.createColumn;
import static net.dillon.dillonlib.client.component.GridBuilder.createRow;
import static net.dillon.dillonlib.task.ClientTasks.isCurrentLevelNull;
import static net.dillon.dillonlib.task.ClientTasks.openScreen;
import static net.dillon.dillonlib.widespread.DillonsWidgets.createMultiLineText;
import static net.dillon.dillonlib.widespread.DillonsWidgets.createOption;
import static net.dillon.qualityofqueso.helper.GuiHelper.drawVisualTimeClock;
import static net.dillon.qualityofqueso.helper.ModHelper.qoqIdentifier;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * A utility screen to change the visual time client-side.
 */
public class VisualTimeScreen extends DillonLibScreen {
    private static final int TEXTURE_SIZE = 24;
    private AbstractWidget visualTime, visualTimeSpeed, syncLocalTime;
    private final Screen parent;

    public VisualTimeScreen(Screen parent) {
        super(DillonsTexts.BLANK);
        this.parent = parent;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        openScreen(this.parent);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
        this.extractBlurredBackground(graphics);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        if (isCurrentLevelNull()) {
            this.extractPanorama(graphics, deltaTicks);
        }
        this.extractMenuBackground(graphics);
    }

    /**
     * @return if the user can override the client time.
     */
    private boolean canOverrideClientTime() {
        return client().visualTime().overrideClientTime;
    }

    /**
     * @return if the user can match with irl time.
     */
    private boolean canMatchWithIrlTime() {
        return client().visualTime().syncLocalTime;
    }

    /**
     * @return the visual time button.
     */
    private AbstractWidget visualTimeButton() {
        return createWidget(
                createOption(
                        ListOptions.visualTime(),
                        120
                ),
                () -> canOverrideClientTime() && !canMatchWithIrlTime() && client().visualTime().visualTimeSpeed == 0
        );
    }

    /**
     * @return the visual time speed button.
     */
    private AbstractWidget visualTimeSpeedButton() {
        return createWidget(
                createOption(
                        ListOptions.visualTimeSpeed(),
                        120
                ),
                () -> canOverrideClientTime() && !canMatchWithIrlTime()
        );
    }

    /**
     * @return the sync local time button.
     */
    private AbstractWidget syncLocalTimeButton() {
        return createWidget(
                createOption(
                        ListOptions.syncLocalTime(),
                        120
                ),
                this::canOverrideClientTime
        );
    }

    /**
     * @return the display visual clock button.
     */
    private AbstractWidget displayVisualClockButton() {
        return createWidget(
                createOption(
                        ListOptions.displayVisualClock(),
                        90
                ),
                () -> canOverrideClientTime() && (isCurrentLevelNull() || this.minecraft.level.dimension() == Level.OVERWORLD)
        );
    }

    @Override
    public void widgets() {
        GridBuilder builder = GridBuilder.create(this)
                .setRowSpacing(12)
                .setColumnSpacing(8);

        builder.fillRowAndCenter(createMultiLineText(Component.translatable("qualityofqueso.gui.visual_time.description")));

        builder.fillRowAndCenter(
                createOption(
                        ListOptions.overrideClientTime(),
                        200
                )
        );

        visualTime = visualTimeButton();
        visualTimeSpeed = visualTimeSpeedButton();
        syncLocalTime = syncLocalTimeButton();

        LinearLayout timeControls = createRow();
        timeControls.addChild(visualTime);
        timeControls.addChild(displayVisualClockButton());

        LinearLayout controls = createColumn();
        controls.addChild(timeControls);
        controls.addChild(visualTimeSpeed);
        controls.addChild(syncLocalTime);
        builder.fillRow(
                controls,
                builder.getGrid().newCellSettings().alignHorizontally(1.24F)
        );

        builder.fillRow(
                Button.builder(Component.translatable("qualityofqueso.gui.save_changes"), button -> {
                    this.onClose();
                }).width(100).build(),
                builder.getGrid().newCellSettings().alignHorizontally(0.619F)
        );

        builder.setVerticalAlignment(0.45F).buildAndAlign();
    }

    @Override
    protected void drawGraphics(GuiGraphicsExtractor graphics) {
        drawVisualTimeClock(
                graphics,
                visualTime.getX() - 48,
                visualTime.getY() - 2,
                TEXTURE_SIZE,
                true
        );

        TextureBuilder.create(
                graphics,
                qoqIdentifier("visual_time/speed"),
                visualTimeSpeed.getX() - 48,
                visualTimeSpeed.getY() + 1,
                TEXTURE_SIZE,
                18
        ).buildAndBlit(true);

        TextureBuilder.create(
                graphics,
                Identifier.withDefaultNamespace("textures/block/daylight_detector" + (client().visualTime().syncLocalTime ? "_inverted" : "") + "_top.png"),
                syncLocalTime.getX() - 48,
                syncLocalTime.getY() - 2,
                TEXTURE_SIZE,
                TEXTURE_SIZE
        ).buildAndBlit(false);
    }
}