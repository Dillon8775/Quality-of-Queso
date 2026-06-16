package net.dillon.qualityofqueso.mixin.client.screen;

import net.dillon.qualityofqueso.helper.ButtonHelper;
import net.dillon.qualityofqueso.platform.MultiLoader;
import net.dillon.qualityofqueso.platform.ReleaseType;
import net.dillon.qualityofqueso.screen.MainMenuScreen;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static net.dillon.qualityofqueso.helper.ModHelper.setScreen;
import static net.dillon.qualityofqueso.helper.ModHelper.universalOptionsInstance;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    @Shadow
    protected abstract int getHorizontalPosition(int par1, int par2, int par3);

    public TitleScreenMixin(Component pTitle) {
        super(pTitle);
    }

    /**
     * Warns the user of a possible beta Quality of Queso version, and adds the main menu button configuration to the screen.
     */
    @Inject(method = "init", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void addButtonsAndWarning(CallbackInfo ci, int copyrightWidth, int copyrightX, int spacing, int topPos, int numberOfButtons, int currentButton, SpriteIconButton language, SpriteIconButton accessibility) {
        if (universalOptionsInstance().getUniversal().menuButton.enabled()) {
            SpriteIconButton menuButton = this.addRenderableWidget(ButtonHelper.createMenuButton(
                    (button) -> setScreen(new MainMenuScreen(this)), true
            ));
            menuButton.setPosition(this.getHorizontalPosition(++currentButton, MultiLoader.getPlatform().isNeoForged() ? 5 : 4, 20), topPos - 24);
        }

        if (!ModConstants.SHOWN_BETA_TOAST && MultiLoader.getPlatform().getReleaseType() != ReleaseType.STABLE) {
            Minecraft.getInstance().gui.toastManager().addToast(new SystemToast(
                            SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
                            Component.translatable("qualityofqueso.toast.title.beta_or_alpha"),
                            Component.translatable("qualityofqueso.toast.beta_or_alpha")));
            ModConstants.SHOWN_BETA_TOAST = true;
        }
    }
}