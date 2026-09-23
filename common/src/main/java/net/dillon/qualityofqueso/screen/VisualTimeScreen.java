package net.dillon.qualityofqueso.screen;

import net.dillon.dillonlib.screen.BasicDillonLibScreen;
import net.dillon.dillonlib.util.Texts;
import net.dillon.qualityofqueso.util.ListOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

import static net.dillon.dillonlib.task.ClientTasks.*;
import static net.dillon.qualityofqueso.helper.GuiHelper.drawVisualTimeClock;
import static net.dillon.qualityofqueso.helper.ModHelper.qoqIdentifier;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * A utility screen to change the visual time client-side.
 */
public class VisualTimeScreen extends BasicDillonLibScreen {
    private static final int TEXTURE_SIZE = 24;
    private AbstractWidget visualTime, visualTimeSpeed, syncLocalTime;
    private final Screen parent;

    public VisualTimeScreen(Screen parent) {
        super(Texts.BLANK);
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
        return this.createWidget(
                ListOptions.visualTime().createButton(
                        Minecraft.getInstance().options,
                        builder().captureWidth(),
                        builder().captureHeight(),
                        120
                ),
                () -> canOverrideClientTime() && !canMatchWithIrlTime() && client().visualTime().visualTimeSpeed == 0
        );
    }

    /**
     * @return the visual time speed button.
     */
    private AbstractWidget visualTimeSpeedButton() {
        return this.createWidget(
                ListOptions.visualTimeSpeed().createButton(
                        Minecraft.getInstance().options,
                        builder().captureWidth(),
                        builder().captureHeight(),
                        120
                ),
                () -> canOverrideClientTime() && !canMatchWithIrlTime()
        );
    }

    /**
     * @return the sync local time button.
     */
    private AbstractWidget syncLocalTimeButton() {
        return this.createWidget(
                ListOptions.syncLocalTime().createButton(
                        Minecraft.getInstance().options,
                        builder().captureWidth(),
                        builder().captureHeight(),
                        120
                ),
                this::canOverrideClientTime
        );
    }

    /**
     * @return the display visual clock button.
     */
    private AbstractWidget displayVisualClockButton() {
        return this.createWidget(
                ListOptions.displayVisualClock().createButton(
                        Minecraft.getInstance().options,
                        builder().captureWidth(),
                        builder().captureHeight(),
                        90
                ),
                () -> canOverrideClientTime() && (this.minecraft.level == null || this.minecraft.level.dimension() == Level.OVERWORLD)
        );
    }

    @Override
    public void widgets() {
        builder().widthCenter().apply();
        builder().heightCenter().apply();

        builder().widthLeft(100).apply();
        builder().heightUp(builder().defaultOffset() * 2).apply();

        this.addRenderableWidget(
                ListOptions.overrideClientTime().createButton(
                        Minecraft.getInstance().options,
                        builder().captureWidth(),
                        builder().captureHeight(),
                        200
                )
        );

        builder().heightDown(32).apply();
        builder().widthRight(60).apply();

        this.visualTime = this.addRenderableWidget(visualTimeButton());

        builder().heightDown(28).apply();

        this.visualTimeSpeed = this.addRenderableWidget(visualTimeSpeedButton());

        builder().heightDown(28).apply();

        this.syncLocalTime = this.addRenderableWidget(syncLocalTimeButton());

        builder().heightDown(32).apply();
        builder().widthRight(12).apply();

        this.addRenderableWidget(Button.builder(Component.translatable("qualityofqueso.gui.save_changes"), button -> {
            this.onClose();
        }).bounds(builder().captureWidth(), builder().captureHeight(), 100, 20).build());

        builder().width(visualTime).apply();
        builder().height(visualTime).apply();
        builder().widthRight(130).apply();

        this.addRenderableWidget(displayVisualClockButton());
    }

    @Override
    protected void drawGraphics(GuiGraphicsExtractor graphics) {
        builder().renderHeightDown(
                builder().heightCenter().pop() - 110
        ).apply();

        builder().textCenterAndHeightDown(graphics, Component.translatable("qualityofqueso.menu.visual_time.description")).apply();
        builder().textCenterAndHeightDown(graphics, Component.translatable("qualityofqueso.gui.visual_time.description.line2")).apply();
        builder().textCenterAndHeightDown(graphics, Component.translatable("qualityofqueso.gui.visual_time.description.line3")).apply();

        builder().renderHeight(visualTime).apply();
        builder().renderWidth(visualTime).apply();
        builder().renderWidthLeft(48).apply();

        drawVisualTimeClock(
                graphics,
                this.minecraft,
                builder().captureRenderWidth(),
                builder().captureRenderHeight() - 2,
                TEXTURE_SIZE,
                true
        );

        builder().renderHeight(visualTimeSpeed).apply();
        builder().renderWidth(visualTimeSpeed).apply();
        builder().renderWidthLeft(48).apply();

        drawSprite(
                graphics,
                qoqIdentifier("visual_time/speed"),
                builder().captureRenderWidth(),
                builder().captureRenderHeight() + 1,
                TEXTURE_SIZE,
                18
        );

        builder().renderHeight(syncLocalTime).apply();
        builder().renderWidth(syncLocalTime).apply();
        builder().renderWidthLeft(48).apply();

        blitTexture(
                graphics,
                Identifier.withDefaultNamespace("textures/block/daylight_detector" + (client().visualTime().syncLocalTime ? "_inverted" : "") + "_top.png"),
                builder().captureRenderWidth(),
                builder().captureRenderHeight() - 2,
                TEXTURE_SIZE,
                TEXTURE_SIZE
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