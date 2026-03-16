package net.dillon.qualityofqueso.mixin.client.screen;

import net.minecraft.client.renderer.Panorama;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static net.dillon.qualityofqueso.util.ModUtil.options;

@Mixin(Panorama.class)
public class PanoramaMixin {

    /**
     * Oh, so like a fortnite battle pass
     */
    @ModifyConstant(method = "extractRenderState", constant = @Constant(floatValue = 0.1F))
    private float spin(float original) {
        return options().misc.fortniteBattlePass ? 2.0F : 0.1F;
    }
}