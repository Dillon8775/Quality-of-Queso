package net.dillon.qualityofqueso.mixin.client.hud;

import net.dillon.qualityofqueso.helper.DebugHudHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public class DebugHudMixin {

    /**
     * Manually adds new {@code debug huds} to the game.
     */
    @Inject(method = "getSystemInformation", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void addDebugHuds(CallbackInfoReturnable<List<String>> cir, long i, long j, long k, long l, List<String> list) {
        DebugHudHelper.appendDebugHudLines(Minecraft.getInstance(), list);
    }
}