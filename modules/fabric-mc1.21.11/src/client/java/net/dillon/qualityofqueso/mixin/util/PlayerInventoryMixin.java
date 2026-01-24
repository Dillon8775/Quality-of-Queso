package net.dillon.qualityofqueso.mixin.util;

import net.dillon.qualityofqueso.util.PickupHudTracker;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.main.QoQ.options;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Shadow
    public abstract ItemStack getStack(int slot);

    /**
     * Tracks items to display total count near hotbar (when thrown).
     */
    @Inject(method = "removeStack", at = @At("HEAD"))
    private void onRemoveStack(int slot, int count, CallbackInfoReturnable<ItemStack> ci) {
        if (!options().displayOnThrow) {
            return;
        }

        ItemStack stack = this.getStack(slot);
        if (!stack.isEmpty()) {
            PickupHudTracker.onPickup(stack.copy());
        }
    }
}