package net.dillon.qualityofqueso.mixin;

import net.minecraft.client.Camera;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.helper.ModHelper.cancelFluidFov;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    public abstract FogType getFluidInCamera();

    /**
     * Removes FOV modification from lava and water.
     */
    @Inject(method = "modifyFovBasedOnDeathOrFluid(FF)F", at = @At("HEAD"), cancellable = true, require = 0)
    private void cancelFluidFOVLegacy(float partialTicks, float fov, CallbackInfoReturnable<Float> cir) {
        cancelFluidFov(this.getFluidInCamera(), fov, cir);
    }
}