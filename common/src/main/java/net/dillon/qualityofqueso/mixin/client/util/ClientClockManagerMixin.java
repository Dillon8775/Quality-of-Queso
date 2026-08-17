package net.dillon.qualityofqueso.mixin.client.util;

import net.dillon.dillonlib.mixinplugin.PredicateSigned;
import net.dillon.qualityofqueso.util.VisualTimeTracker;
import net.minecraft.client.ClientClockManager;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

@PredicateSigned
@Mixin(ClientClockManager.ClientClockInstance.class)
public class ClientClockManagerMixin {

    /**
     * Changes overworld time {@code client-side} for visual-only time effects.
     */
    @Inject(method = "totalTicks", at = @At("HEAD"), cancellable = true)
    private void overrideOverworldClockTime(CallbackInfoReturnable<Long> cir) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!modEnabled(minecraft) || !client().visualTime().overrideClientTime) {
            return;
        }

        cir.setReturnValue(VisualTimeTracker.getVisualTime(minecraft));
    }
}