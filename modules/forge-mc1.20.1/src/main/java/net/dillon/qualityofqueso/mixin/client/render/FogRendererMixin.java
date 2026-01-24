package net.dillon.qualityofqueso.mixin.client.render;

import net.minecraft.client.renderer.FogRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static net.dillon.qualityofqueso.main.QoQ.options;

/**
 * Removes fog from the game when using the keybind.
 */
@OnlyIn(Dist.CLIENT)
@Mixin(FogRenderer.class)
public class FogRendererMixin {

    @Redirect(method = "setupFog", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/FogRenderer$FogData;end:F", ordinal = 9, opcode = Opcodes.PUTFIELD))
    private static void modifyEndValue(FogRenderer.FogData fogData, float original) {
        if (!options().fog) {
            fogData.end = Integer.MAX_VALUE;
        } else {
            fogData.end = original;
        }
    }

    @Redirect(method = "setupFog", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/FogRenderer$FogData;end:F", ordinal = 11, opcode = Opcodes.PUTFIELD))
    private static void modifyEndingValue(FogRenderer.FogData fogData, float original) {
        if (!options().fog) {
            fogData.end = Integer.MAX_VALUE;
        } else {
            fogData.end = original;
        }
    }
}