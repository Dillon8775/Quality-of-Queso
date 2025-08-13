package net.dillon.qualityofqueso.mixin;

import net.dillon.qualityofqueso.util.ButtonUtil;
import net.dillon.qualityofqueso.util.ModTexts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
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

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/ColorHelper;withAlpha(FI)I"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void renderTooltips(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci, float f) {
        ButtonUtil.drawTooltipAndTexture(ModTexts.CONFIGURE_QOQ, ButtonUtil.CHEESE_WHEEL, context, this.textRenderer, this.settingsButton, mouseX, mouseY, f);
    }
}