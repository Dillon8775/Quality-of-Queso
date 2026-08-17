package net.dillon.qualityofqueso.mixin.client.screen;

import net.dillon.dillonlib.mixinplugin.PredicateSigned;
import net.dillon.dillonlib.platform.info.PlatformRelease;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.dillon.qualityofqueso.platform.QualityOfQuesoPlatforms;
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
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static net.dillon.qualityofqueso.option.OptionInstances.client;

@PredicateSigned
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    public TitleScreenMixin(Component pTitle) {
        super(pTitle);
    }

    /**
     * Warns the user of a possible beta Quality of Queso version.
     */
    @Inject(method = "init", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void sendWarning(CallbackInfo ci, int copyrightWidth, int copyrightX, int spacing, int topPos, int numberOfButtons, int currentButton, SpriteIconButton language, SpriteIconButton accessibility) {
        if (!ModConstants.SHOWN_BETA_TOAST && client().accessibility().betaWarning && QualityOfQuesoPlatforms.getPlatform().platformRelease() != PlatformRelease.STABLE) {
            Minecraft.getInstance().gui.toastManager().addToast(new SystemToast(
                            SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
                            Component.translatable("qualityofqueso.toast.title.beta_or_alpha"),
                            Component.translatable("qualityofqueso.toast.beta_or_alpha")));
            ModConstants.SHOWN_BETA_TOAST = true;
        }
    }
}