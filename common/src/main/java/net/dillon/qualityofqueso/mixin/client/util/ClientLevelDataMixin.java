package net.dillon.qualityofqueso.mixin.client.util;

import net.dillon.qualityofqueso.util.VisualTimeTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;
import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;

@Mixin(ClientLevel.ClientLevelData.class)
public class ClientLevelDataMixin {

    /**
     * Changes overworld time {@code client-side} for visual-only time effects.
     */
    @Inject(method = "getDayTime", at = @At("HEAD"), cancellable = true)
    private void overrideOverworldClockTime(CallbackInfoReturnable<Long> ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!modEnabled(minecraft) || !clientOptionsInstance().getVisualTimeOptions().overrideClientTime) {
            return;
        }

        ci.setReturnValue(VisualTimeTracker.getVisualTime(minecraft));
    }
}