package net.dillon.qualityofqueso.mixin.client.screen;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.reporting.DraftIconButton;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.dillonlib.task.ClientTasks.drawTooltip;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

@Mixin(PauseScreen.class)
public class PauseScreenMixin extends Screen {
    @Unique
    private DraftIconButton disconnectButton;

    public PauseScreenMixin(Component pTitle) {
        super(pTitle);
    }

    /**
     * Grays out the disconnect button if anti-rage quit is enabled.
     * @return the new button.
     */
    @ModifyExpressionValue(method = "createPauseMenu", at = @At(value = "NEW", target = "(IILnet/minecraft/network/chat/Component;Lnet/minecraft/client/gui/components/Button$OnPress;Lnet/minecraft/client/gui/components/Tooltip;Ljava/util/function/Predicate;)Lnet/minecraft/client/gui/screens/reporting/DraftIconButton;"))
    private DraftIconButton grayoutButton(DraftIconButton button) {
        if (!client().misc().antiRageQuit) {
            return button;
        }

        button.active = false;
        disconnectButton = button;
        return button;
    }

    /**
     * Manually renders a tooltip for the disconnect button if the anti-rage quit feature is enabled, because setting a tooltip doesn't work.
     */
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void tooltipForDisconnectButton(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        if (!client().misc().antiRageQuit || !client().general().tooltips.enabled() || disconnectButton == null || !disconnectButton.isHovered()) {
            return;
        }

        drawTooltip(Component.translatable("qualityofqueso.gui.disconnect"), graphics);
    }
}