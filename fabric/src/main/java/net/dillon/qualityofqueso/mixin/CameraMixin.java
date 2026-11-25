package net.dillon.qualityofqueso.mixin;

import net.dillon.dillonlib.mixinplugin.Predicated;
import net.minecraft.client.Camera;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.helper.ModHelper.cancelFluidFov;

@Predicated
@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    public abstract FogType getFogType();

    /**
     * Removes FOV modification from lava and water.
     */
    @Inject(method = "modifyFovBasedOnDeathOrFluid(FF)F", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getFogType()Lnet/minecraft/world/level/material/FogType;"), cancellable = true, require = 0)
    private void cancelFluidFOVLegacy(float partialTicks, float fov, CallbackInfoReturnable<Float> cir) {
        cancelFluidFov(this.getFogType(), fov, cir);
    }
}