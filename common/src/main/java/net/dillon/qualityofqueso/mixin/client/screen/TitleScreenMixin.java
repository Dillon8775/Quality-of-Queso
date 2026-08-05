package net.dillon.qualityofqueso.mixin.client.screen;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.dillon.dillonlib.mixinplugin.PredicateSigned;
import net.dillon.dillonlib.platform.info.PlatformRelease;
import net.dillon.dillonlib.task.ClientTasks;
import net.dillon.qualityofqueso.helper.ButtonHelper;
import net.dillon.qualityofqueso.platform.ModReferences;
import net.dillon.qualityofqueso.platform.QualityOfQuesoPlatforms;
import net.dillon.qualityofqueso.screen.MainMenuScreen;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static net.dillon.dillonlib.task.ClientTasks.openScreen;
import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;
import static net.dillon.qualityofqueso.helper.ModHelper.universalOptionsInstance;

@PredicateSigned
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    @Shadow
    protected abstract int getHorizontalPosition(int par1, int par2, int par3);
    @Unique
    private SpriteIconButton menuButton;

    public TitleScreenMixin(Component pTitle) {
        super(pTitle);
    }

    /**
     * Increases the maximum amount of buttons that should be displayed on the title screen.
     */
    @Definition(id = "numberOfButtons", local = @Local(type = int.class, name = "numberOfButtons"))
    @Expression("numberOfButtons = ?")
    @Inject(method = "init", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER))
    private void adjustAmountOfIconButtons(CallbackInfo ci, @Local(name = "numberOfButtons") LocalIntRef numberOfButtons) {
        if (universalOptionsInstance().menuButton.enabled() && (numberOfButtons.get() > 3 || ModReferences.isModLoaded(ModReferences.MOD_MENU))) {
            numberOfButtons.set(numberOfButtons.get() + 1);
        }
    }

    /**
     * Warns the user of a possible beta Quality of Queso version, and adds the main menu button configuration to the screen.
     */
    @Inject(method = "init", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void addButtonsAndWarning(CallbackInfo ci, int copyrightWidth, int copyrightX, int spacing, int topPos, int numberOfButtons, int currentButton, SpriteIconButton language, SpriteIconButton accessibility) {
        if (universalOptionsInstance().menuButton.enabled()) {
            this.menuButton = this.addRenderableWidget(ButtonHelper.createMenuButton(
                    (button) -> openScreen(new MainMenuScreen(this)), true
            ));
            this.menuButton.setPosition(this.getHorizontalPosition(++currentButton, numberOfButtons, 20), topPos - 24);
        }

        if (!ModConstants.SHOWN_BETA_TOAST && clientOptionsInstance().getAccessibilityOptions().betaWarning && QualityOfQuesoPlatforms.getPlatform().platformRelease() != PlatformRelease.STABLE) {
            Minecraft.getInstance().gui.toastManager().addToast(new SystemToast(
                            SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
                            Component.translatable("qualityofqueso.toast.title.beta_or_alpha"),
                            Component.translatable("qualityofqueso.toast.beta_or_alpha")));
            ModConstants.SHOWN_BETA_TOAST = true;
        }
    }

    /**
     * Renders the update icon on top of the menu button.
     */
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void renderUpdateIcon(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        ClientTasks.renderUpdateIconOnButton(graphics, this.menuButton, ModConstants.HAS_UPDATE);
    }
}