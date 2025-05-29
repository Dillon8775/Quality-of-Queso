package net.dillon.qualityofqueso.mixin.client;

import net.dillon.qualityofqueso.QualityOfQuesoOptionsScreen;
import net.dillon.qualityofqueso.QuesoTexts;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {TitleScreen.class, GameMenuScreen.class})
public class ScreensMixin extends Screen {
    @Unique
    private ButtonWidget settingsButton;

    public ScreensMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        this.settingsButton = this.addDrawableChild(ButtonWidget.builder(QuesoTexts.BLANK, (button) -> {
            this.client.setScreen(new QualityOfQuesoOptionsScreen(this));
        }).dimensions(
                (Screen)this instanceof TitleScreen ? this.width / 2 - 124 : this.width / 2 - 4 - 120 - 2,
                (Screen)this instanceof TitleScreen ? this.height / 4 + 96 : this.height / 4 + 96 - 16,
                20, 20).build());
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderTooltips(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (this.settingsButton.isHovered()) {
            context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(Text.translatable("qualityofqueso.gui.options.tooltip"), 200), mouseX, mouseY);
        }

        context.drawTexture(RenderLayer::getGuiTextured, Identifier.of("qualityofqueso:textures/gui/cheesewheel.png"), settingsButton.getX() + 1, settingsButton.getY() + 1, 0.0F, 0.0F, 18, 18, 18, 18);
    }
}