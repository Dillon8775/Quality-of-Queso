package net.dillon.qualityofqueso.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static net.dillon.qualityofqueso.main.QoQ.options;

// Set priority to 1005 so this applies last.
@OnlyIn(Dist.CLIENT)
@Mixin(value = FogRenderer.class, priority = 1005)
public class FogRendererMixin {

	/**
	 * Removes fog from the game when using the keybind.
	 */
	@Inject(method = "setupFog", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;getDevice()Lcom/mojang/blaze3d/systems/GpuDevice;"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void modifyFogEnd(Camera p_407701_, int p_407051_, DeltaTracker p_406871_, float p_409892_, ClientLevel p_407936_, CallbackInfoReturnable<Vector4f> cir, float f, Vector4f vector4f, float f1, FogType fogtype, Entity entity, FogData fogData, float f2) {
		if (entity instanceof LivingEntity livingEntity &&
				!livingEntity.hasEffect(MobEffects.BLINDNESS) &&
				!livingEntity.hasEffect(MobEffects.DARKNESS) &&
				!options().fog &&
				fogtype != FogType.WATER &&
				fogtype != FogType.LAVA &&
				fogtype != FogType.POWDER_SNOW) {
			fogData.renderDistanceEnd = Integer.MAX_VALUE;
			fogData.environmentalEnd = Integer.MAX_VALUE;
		}
	}
}