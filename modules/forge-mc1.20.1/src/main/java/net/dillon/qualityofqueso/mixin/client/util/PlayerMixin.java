package net.dillon.qualityofqueso.mixin.client.util;

import net.dillon.qualityofqueso.util.PickupHudTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.main.QoQ.modEnabled;
import static net.dillon.qualityofqueso.main.QoQ.options;

@Mixin(Player.class)
public class PlayerMixin {

    /**
     * Tracks items to display total count near hotbar (when thrown {@code from a GUI screen}).
     */
    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("TAIL"))
    private void onThrowFromGui(ItemStack stack, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
        if (!modEnabled(Minecraft.getInstance()) || !options().displayOnThrow) {
            return;
        }

        ItemEntity itemEntity = cir.getReturnValue();
        if (itemEntity == null) {
            return;
        }

        PickupHudTracker.setStack(itemEntity.getItem().copy());
    }
}