package net.dillon.qualityofqueso.mixin.client.screen;

import net.dillon.qualityofqueso.platform.MultiLoader;
import net.dillon.qualityofqueso.util.ButtonUtil;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.util.ModUtil.options;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

    public TitleScreenMixin(Component pTitle) {
        super(pTitle);
    }

    /**
     * Adds the Quality of Queso configuration button to the title screen.
     */
    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        if (options().accessibility.qoqButtons.enabled()) {
            SpriteIconButton settingsButton = this.addRenderableWidget(ButtonUtil.initializeButton(this.minecraft, this));
            settingsButton.setPosition(this.width / 2 + 128, this.height / 4 + 132 + (MultiLoader.PLATFORM.getPlatformName().equals("NeoForged") ? 8 : 0));
        }
    }
}