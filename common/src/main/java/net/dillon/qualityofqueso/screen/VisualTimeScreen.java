package net.dillon.qualityofqueso.screen;

import net.dillon.dillonlib.util.Texts;
import net.dillon.qualityofqueso.util.ListOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;

import static net.dillon.dillonlib.task.ClientTasks.openScreen;
import static net.dillon.qualityofqueso.helper.GuiHelper.drawVisualTimeClock;
import static net.dillon.qualityofqueso.helper.ModHelper.qoqIdentifier;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

/**
 * A utility screen to change the visual time client-side.
 */
public class VisualTimeScreen extends Screen {
    private static final int SIZE = 24;
    private AbstractWidget overrideClientTime, visualTime, displayVisualClock, visualTimeSpeed, syncLocalTime;
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
    protected void init() {
        int width = this.width / 2 - 40;
        this.overrideClientTime = this.addRenderableWidget(ListOptions.overrideClientTime().createButton(Minecraft.getInstance().options, this.width / 2 - 100, this.height / 2 - 48, 200));
        this.visualTime = this.addRenderableWidget(ListOptions.visualTime().createButton(Minecraft.getInstance().options, width, this.overrideClientTime.getY() + 32, 120));
        this.displayVisualClock = this.addRenderableWidget(ListOptions.displayVisualClock().createButton(Minecraft.getInstance().options, this.visualTime.getX() + 130, this.visualTime.getY(), 90));
        this.visualTimeSpeed = this.addRenderableWidget(ListOptions.visualTimeSpeed().createButton(Minecraft.getInstance().options, width, this.visualTime.getY() + 28, 120));
        this.syncLocalTime = this.addRenderableWidget(ListOptions.syncLocalTime().createButton(Minecraft.getInstance().options, width, this.visualTimeSpeed.getY() + 28, 120));

        this.addRenderableWidget(Button.builder(Component.translatable("qualityofqueso.gui.save_changes"), button -> {
            this.onClose();
        }).bounds(this.syncLocalTime.getX() + 12, this.syncLocalTime.getY() + 32, 100, 20).build());
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

        graphics.text(this.font, Component.translatable("qualityofqueso.gui.visual_time.description"), this.width / 2 - 105, this.height / 2 - 110, CommonColors.WHITE);
        graphics.text(this.font, Component.translatable("qualityofqueso.gui.visual_time.description.line2"), this.width / 2 - 145, this.height / 2 - 90, CommonColors.WHITE);
        graphics.text(this.font, Component.translatable("qualityofqueso.gui.visual_time.description.line3"), this.width / 2 - 40, this.height / 2 - 70, CommonColors.WHITE);

        drawVisualTimeClock(graphics, this.minecraft, this.visualTime.getX() - 48, this.visualTime.getY() - 2, 24);

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, qoqIdentifier("visual_time/speed"), this.visualTimeSpeed.getX() - 48, this.visualTimeSpeed.getY() + 1, 24, 18);
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("textures/block/daylight_detector" + (client().visualTime().syncLocalTime ? "_inverted" : "") + "_top.png"), this.syncLocalTime.getX() - 48, this.syncLocalTime.getY() - 2, 0.0F, 0.0F, SIZE, SIZE, SIZE, SIZE);

        boolean overrideClientTime = client().visualTime().overrideClientTime;
        boolean matchWithIRLTime = client().visualTime().syncLocalTime;
        this.visualTime.active = overrideClientTime && client().visualTime().visualTimeSpeed == 0 && !matchWithIRLTime;
        this.displayVisualClock.active = overrideClientTime;
        this.visualTimeSpeed.active = overrideClientTime && !matchWithIRLTime;
        this.syncLocalTime.active = overrideClientTime;

        this.extractBlurredBackground(graphics);
    }
}