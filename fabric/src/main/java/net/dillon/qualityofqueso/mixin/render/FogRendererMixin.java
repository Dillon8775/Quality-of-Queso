package net.dillon.qualityofqueso.mixin.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dillon.qualityofqueso.util.ModUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

    /**
     * Handles fog modification.
     */
    @Inject(method = "setupFog", at = @At("TAIL"))
    private static void modifyFogEnd(Camera camera, FogRenderer.FogMode fogMode, float f, boolean bl, float g, CallbackInfo ci) {
        RenderSystem.setShaderFogEnd(
                ModUtil.handleFog(Minecraft.getInstance().player, camera.getFluidInCamera(), fogMode, f, bl)
        );
    }
}