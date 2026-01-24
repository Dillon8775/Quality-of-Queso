package net.dillon.qualityofqueso.mixin.render;

import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static net.dillon.qualityofqueso.main.QoQ.options;

// Set priority to 1005 so this applies last.
@Mixin(value = FogRenderer.class, priority = 1005)
public class FogRendererMixin {

	/**
	 * Removes fog from the game.
	 */
	@Inject(method = "applyFog(Lnet/minecraft/client/render/Camera;ILnet/minecraft/client/render/RenderTickCounter;FLnet/minecraft/client/world/ClientWorld;)Lorg/joml/Vector4f;", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;getDevice()Lcom/mojang/blaze3d/systems/GpuDevice;"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void modifyFogEnd(Camera camera, int viewDistance, RenderTickCounter renderTickCounter, float f, ClientWorld clientWorld, CallbackInfoReturnable<Vector4f> cir, float g, Vector4f vector4f, float h, CameraSubmersionType cameraSubmersionType, Entity entity, FogData fogData, float i) {
		if (entity instanceof LivingEntity livingEntity &&
				!livingEntity.hasStatusEffect(StatusEffects.BLINDNESS) &&
				!livingEntity.hasStatusEffect(StatusEffects.DARKNESS) &&
				!options().fog &&
				cameraSubmersionType != CameraSubmersionType.WATER &&
				cameraSubmersionType != CameraSubmersionType.LAVA &&
				cameraSubmersionType != CameraSubmersionType.POWDER_SNOW) {
			fogData.renderDistanceEnd = Integer.MAX_VALUE;
			fogData.environmentalEnd = Integer.MAX_VALUE;
		}
	}
}