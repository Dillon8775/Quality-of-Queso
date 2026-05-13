package net.dillon.qualityofqueso.mixin;

import net.dillon.qualityofqueso.helper.ModHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

    /**
     * Removes fog from the game when using the keybind.
     */
    @Inject(method = "setupFog", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;getDevice()Lcom/mojang/blaze3d/systems/GpuDevice;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void modifyFogEnd(Camera camera, int i, DeltaTracker deltaTracker, float f, ClientLevel clientLevel, CallbackInfoReturnable<Vector4f> cir, float g, Vector4f vector4f, float h, FogType fogType, Entity entity, FogData fogData, float j) {
        ModHelper.handleFog(entity, fogType, fogData);
    }
}