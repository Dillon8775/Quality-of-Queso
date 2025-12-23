package net.dillon.qualityofqueso.mixin.client;

import net.dillon.qualityofqueso.option.ModClientOptions;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

    public TitleScreenMixin(Component pTitle) {
        super(pTitle);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        if (ModClientOptions.QOQ_BUTTONS.get().everywhere() || ModClientOptions.QOQ_BUTTONS.get().titleOnly()) {
            this.addRenderableWidget(ButtonUtil.initializeButton(this.minecraft, this, this.width / 2 + 128, this.height / 4 + 132));
        }
    }
}