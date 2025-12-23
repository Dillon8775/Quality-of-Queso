package net.dillon.qualityofqueso.mixin;

import net.dillon.qualityofqueso.main.QoQ;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

    public TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        if (QoQ.options().qoqButtons.everywhere() || QoQ.options().qoqButtons.titleOnly()) {
            TextIconButtonWidget settingsButton = this.addDrawableChild(ButtonUtil.initializeButton(this.client, this));
            settingsButton.setPosition(this.width / 2 + 128, this.height / 4 + 132);
        }
    }
}