package net.dillon.qualityofqueso.mixin.client.screen;

import net.dillon.qualityofqueso.helper.ButtonHelper;
import net.dillon.qualityofqueso.platform.MultiLoader;
import net.dillon.qualityofqueso.platform.ReleaseType;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;
import static net.dillon.qualityofqueso.helper.ModHelper.universalOptionsInstance;

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
        if (universalOptionsInstance().menuButton.enabled()) {
            SpriteIconButton menuButton = ButtonHelper.createMainMenuButton(this);
            this.addRenderableWidget(menuButton);
            menuButton.setPosition(this.width / 2 + 128, this.height / 4 + 132 + (MultiLoader.getPlatform().isNeoForged() ? 8 : 0));
        }

        if (!ModConstants.SHOWN_BETA_TOAST && clientOptionsInstance().getAccessibilityOptions().betaWarning && MultiLoader.getPlatform().getReleaseType() != ReleaseType.STABLE) {
            Minecraft.getInstance().getToasts().addToast(
                    SystemToast.multiline(Minecraft.getInstance(),
                            SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
                            Component.translatable("qualityofqueso.toast.title.beta_or_alpha"),
                            Component.translatable("qualityofqueso.toast.beta_or_alpha")));
            ModConstants.SHOWN_BETA_TOAST = true;
        }
    }
}