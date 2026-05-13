package net.dillon.qualityofqueso.mixin.client.screen;

import net.dillon.qualityofqueso.helper.ButtonHelper;
import net.dillon.qualityofqueso.platform.MultiLoader;
import net.dillon.qualityofqueso.screen.MainMenuScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.helper.ModHelper.uoptions;

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
        if (uoptions().main.menuButton.enabled()) {
            this.addRenderableWidget(ButtonHelper.createMenuButton(
                    this.width / 2 + 128,
                    this.height / 4 + 132 + (MultiLoader.getPlatform().isNeoForged() ? 6 : 0),
                    (button) -> this.minecraft.setScreen(new MainMenuScreen(this))
            ));
        }
    }
}