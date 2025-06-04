package net.dillon.qualityofqueso.mixin.client;

import net.dillon.qualityofqueso.QualityOfQuesoClient;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {
    @Shadow @Final
    private boolean showMenu;
    @Unique
    private ButtonWidget settingsButton;

    public GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        if (QualityOfQuesoClient.options().showConfigurationButton && this.showMenu) {
            this.settingsButton = this.addDrawableChild(ButtonUtil.initializeButton(this.client, this, this.width / 2 + 108, this.height / 4 + 72 - 16));
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderTooltips(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (QualityOfQuesoClient.options().showConfigurationButton && this.showMenu) {
            ButtonUtil.drawTooltipAndTexture(context, this.textRenderer, this.settingsButton, mouseX, mouseY);
        }
    }
}