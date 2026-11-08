package net.dillon.qualityofqueso.mixin.client.screen;

import net.dillon.dillonlib.mixinplugin.Predicated;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.helper.GuiHelper.drawTooltip;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

@Predicated
@Mixin(value = PauseScreen.class, priority = 1010)
public class PauseScreenMixin extends Screen {
    @Shadow
    @Final
    private boolean showPauseMenu;
    @Shadow
    private Button disconnectButton;

    public PauseScreenMixin(Component pTitle) {
        super(pTitle);
    }

    /**
     * Renders toolips and textures over top of the {@code Quality of Queso buttons.}
     */
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void renderTooltipsAndTextures(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (!this.showPauseMenu) {
            return;
        }

        if (client().misc().antiRageQuit && this.disconnectButton != null) {
            this.disconnectButton.active = false;
            if (client().general().tooltips.enabled() && this.disconnectButton.isHovered()) {
                drawTooltip(Component.translatable("qualityofqueso.gui.disconnect"), graphics, mouseX, mouseY);
            }
        }
    }
}