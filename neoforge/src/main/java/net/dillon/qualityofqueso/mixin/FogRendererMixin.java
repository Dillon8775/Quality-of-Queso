package net.dillon.qualityofqueso.mixin;

import net.dillon.qualityofqueso.helper.ModHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.ByteBuffer;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    @Shadow
    private void updateBuffer(ByteBuffer buffer, int position, Vector4f fogColor, float environmentalStart, float environmentalEnd, float renderDistanceStart, float renderDistanceEnd, float skyEnd, float cloudEnd) {
        throw new AssertionError();
    }

    /**
     * Removes fog from the game.
     */
    @Redirect(method = "setupFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/fog/FogRenderer;updateBuffer(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V"))
    @Deprecated
    private void modifyFogEnd(
            FogRenderer instance,
            ByteBuffer buffer,
            int position,
            Vector4f fogColor,
            float environmentalStart,
            float environmentalEnd,
            float renderDistanceStart,
            float renderDistanceEnd,
            float skyEnd,
            float cloudEnd
    ) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Entity entity = camera.entity();
        FogType fogType = camera.getFluidInCamera();

        FogData fogData = new FogData();
        fogData.environmentalEnd = environmentalEnd;
        fogData.renderDistanceEnd = renderDistanceEnd;

        ModHelper.handleFog(entity, fogType, fogData);

        this.updateBuffer(
                buffer,
                position,
                fogColor,
                environmentalStart,
                fogData.environmentalEnd,
                renderDistanceStart,
                fogData.renderDistanceEnd,
                skyEnd,
                cloudEnd
        );
    }
}