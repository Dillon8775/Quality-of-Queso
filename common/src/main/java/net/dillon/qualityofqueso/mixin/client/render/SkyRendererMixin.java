package net.dillon.qualityofqueso.mixin.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SkyRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;
import static net.dillon.qualityofqueso.helper.ModHelper.options;

@Mixin(SkyRenderer.class)
public class SkyRendererMixin {

    /**
     * Prevents the {@code "dark disc} from being rendered, if the user chooses to do so.
     */
    @Inject(method = "shouldRenderDarkDisc", at = @At("HEAD"), cancellable = true)
    private void cancelOutDarkDisc(float deltaPartialTick, ClientLevel level, CallbackInfoReturnable<Boolean> cir) {
        if (!modEnabled(Minecraft.getInstance()) || options().accessibility.darkDisc) {
            return;
        }

        cir.setReturnValue(false);
    }
}