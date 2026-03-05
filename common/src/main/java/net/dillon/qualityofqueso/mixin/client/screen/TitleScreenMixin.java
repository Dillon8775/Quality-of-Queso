package net.dillon.qualityofqueso.mixin.client.screen;

import net.dillon.qualityofqueso.util.ButtonUtil;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

    public TitleScreenMixin(Component pTitle) {
        super(pTitle);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        SpriteIconButton settingsButton = this.addRenderableWidget(ButtonUtil.initializeButton(this.minecraft, this));
        settingsButton.setPosition(this.width / 2 + 128, this.height / 4 + 132);
    }
}