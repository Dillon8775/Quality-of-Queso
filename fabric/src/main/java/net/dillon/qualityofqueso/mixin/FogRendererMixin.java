package net.dillon.qualityofqueso.mixin;

import net.dillon.qualityofqueso.helper.ModHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
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
    @Inject(method = "setupFog", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void modifyFogEnd(Camera camera, int renderDistanceInChunks, DeltaTracker deltaTracker, float darkenWorldAmount, ClientLevel level, CallbackInfoReturnable<FogData> cir, float partialTickTime, float renderDistanceInBlocks, FogType fogType, Entity entity, FogData fog, float renderDistanceFogSpan) {
        ModHelper.handleFog(entity, fogType, fog);
    }
}