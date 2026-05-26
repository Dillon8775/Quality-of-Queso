package net.dillon.qualityofqueso.screen;

import net.dillon.qualityofqueso.helper.ModHelper;
import net.dillon.qualityofqueso.util.ListOptions;
import net.dillon.qualityofqueso.util.ModTexts;
import net.dillon.qualityofqueso.util.VisualTimeTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;

import java.util.Locale;

import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;
import static net.dillon.qualityofqueso.helper.ModHelper.ofQoQ;

/**
 * A utility screen to change the visual time client-side.
 */
public class VisualTimeScreen extends Screen {
    private static final int SIZE = 24;
    private AbstractWidget overrideClientTime, visualTime, visualTimeSpeed, matchWithIRLTime;
    private final Screen parent;

    public VisualTimeScreen(Screen parent) {
        super(ModTexts.BLANK);
        this.parent = parent;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        if (this.minecraft.level == null) {
            this.renderPanorama(graphics, deltaTicks);
        }
        this.renderMenuBackground(graphics);
    }

    @Override
    protected void init() {
        int width = this.width / 2 - 32;
        this.overrideClientTime = this.addRenderableWidget(ListOptions.overrideClientTime().createButton(Minecraft.getInstance().options, this.width / 2 - 100, this.height / 2 - 48, 200));
        this.visualTime = this.addRenderableWidget(ListOptions.visualTime().createButton(Minecraft.getInstance().options, width, this.overrideClientTime.getY() + 32, 120));
        this.visualTimeSpeed = this.addRenderableWidget(ListOptions.visualTimeSpeed().createButton(Minecraft.getInstance().options, width, this.visualTime.getY() + 28, 120));
        this.matchWithIRLTime = this.addRenderableWidget(ListOptions.syncLocalTime().createButton(Minecraft.getInstance().options, width, this.visualTimeSpeed.getY() + 28, 120));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        super.render(graphics, mouseX, mouseY, deltaTicks);

        graphics.drawString(this.font, Component.translatable("qualityofqueso.gui.visual_time.description"), this.width / 2 - 105, this.height / 2 - 110, CommonColors.WHITE);
        graphics.drawString(this.font, Component.translatable("qualityofqueso.gui.visual_time.description.line2"), this.width / 2 - 145, this.height / 2 - 90, CommonColors.WHITE);
        graphics.drawString(this.font, Component.translatable("qualityofqueso.gui.visual_time.description.line3"), this.width / 2 - 40, this.height / 2 - 70, CommonColors.WHITE);

        long visualTime = VisualTimeTracker.getVisualTime(this.minecraft);
        int clockFrame = Math.floorMod((int) ((visualTime * 64L) / 24000L), 64);
        ResourceLocation clockTexture = ResourceLocation.withDefaultNamespace("textures/item/clock_" + String.format(Locale.ROOT, "%02d", clockFrame) + ".png");
        graphics.blit(clockTexture, this.visualTime.getX() - 48, this.visualTime.getY() - 2, 0.0F, 0.0F, SIZE, SIZE, SIZE, SIZE);

        graphics.blit(ofQoQ("textures/gui/sprites/visual_time/speed.png"), this.visualTimeSpeed.getX() - 48, this.visualTimeSpeed.getY() + 1, 0.0F, 0.0F, 24, 18, 24, 18);
        graphics.blit(ResourceLocation.withDefaultNamespace("textures/block/daylight_detector" + (clientOptionsInstance().getVisualTimeOptions().syncLocalTime ? "_inverted" : "") + "_top.png"), this.matchWithIRLTime.getX() - 48, this.matchWithIRLTime.getY() - 2, 0.0F, 0.0F, SIZE, SIZE, SIZE, SIZE);

        boolean overrideClientTime = ModHelper.clientOptionsInstance().getVisualTimeOptions().overrideClientTime;
        boolean matchWithIRLTime = ModHelper.clientOptionsInstance().getVisualTimeOptions().syncLocalTime;
        this.visualTime.active = overrideClientTime && ModHelper.clientOptionsInstance().getVisualTimeOptions().visualTimeSpeed == 0 && !matchWithIRLTime;
        this.visualTimeSpeed.active = overrideClientTime && !matchWithIRLTime;
        this.matchWithIRLTime.active = overrideClientTime;
    }
}