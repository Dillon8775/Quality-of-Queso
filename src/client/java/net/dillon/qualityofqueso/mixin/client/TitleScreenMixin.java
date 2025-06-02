package net.dillon.qualityofqueso.mixin.client;

import net.dillon.qualityofqueso.util.ButtonUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    @Unique
    private ButtonWidget settingsButton;

    public TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        this.settingsButton = this.addDrawableChild(ButtonUtil.initializeButton(this.client, this, this.width / 2 + 128, this.height / 4 + 132));
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderTooltips(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        ButtonUtil.drawTooltipAndTexture(context, this.textRenderer, this.settingsButton, mouseX, mouseY);
    }
}