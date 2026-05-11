package net.dillon.qualityofqueso.mixin.client.util;

import net.dillon.qualityofqueso.util.VisualTimeTracker;
import net.minecraft.client.ClientClockManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;
import static net.dillon.qualityofqueso.helper.ModHelper.options;

@Mixin(ClientClockManager.class)
public class ClientClockManagerMixin {

    /**
     * Changes overworld time {@code client-side} for visual-only time effects.
     */
    @Inject(method = "getTotalTicks", at = @At("HEAD"), cancellable = true)
    private void overrideOverworldClockTime(Holder<WorldClock> definition, CallbackInfoReturnable<Long> cir) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!modEnabled(minecraft) || !definition.is(WorldClocks.OVERWORLD) || !options().visualTime.overrideClientTime) {
            return;
        }

        cir.setReturnValue(VisualTimeTracker.getVisualTime(minecraft));
    }
}