package net.dillon.qualityofqueso.mixin.client;

import net.dillon.qualityofqueso.util.PickupHudTracker;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(Inventory.class)
public abstract class PlayerInventoryMixin {
    @Shadow
    public abstract ItemStack getItem(int pIndex);

    /**
     * Tracks items to display total count near hotbar (when thrown).
     */
    @Inject(method = "removeItem", at = @At("HEAD"))
    private void onRemoveStack(int slot, int count, CallbackInfoReturnable<ItemStack> ci) {
        ItemStack stack = this.getItem(slot);
        if (!stack.isEmpty()) {
            PickupHudTracker.onPickup(stack.copy());
        }
    }
}