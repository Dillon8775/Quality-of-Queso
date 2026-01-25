package net.dillon.qualityofqueso.mixin.util;

import net.dillon.qualityofqueso.util.PickupHudTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.main.QoQ.modEnabled;
import static net.dillon.qualityofqueso.main.QoQ.options;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    /**
     * Tracks items to display total count near hotbar (when thrown {@code from a GUI screen}).
     */
    @Inject(method = "dropItem", at = @At("TAIL"))
    private void onThrowFromGui(ItemStack stack, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
        if (!modEnabled(MinecraftClient.getInstance()) || !options().displayOnThrow) {
            return;
        }

        ItemEntity itemEntity = cir.getReturnValue();
        if (itemEntity == null) {
            return;
        }

        PickupHudTracker.setStack(itemEntity.getStack().copy());
    }
}