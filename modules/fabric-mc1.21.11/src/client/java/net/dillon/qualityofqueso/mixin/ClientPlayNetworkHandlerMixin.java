package net.dillon.qualityofqueso.mixin;

import net.dillon.qualityofqueso.util.PickupHudTracker;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.main.QoQ.options;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Shadow
    private ClientWorld world;

    /**
     * Tracks the item that was picked up to display near the hotbar.
     */
    @Inject(method = "onItemPickupAnimation", at = @At("HEAD"))
    private void trackPickedUpItem(ItemPickupAnimationS2CPacket packet, CallbackInfo ci) {
        if (!options().displayOnPickup) {
            return;
        }

        Entity entity = this.world.getEntityById(packet.getEntityId());
        if (!(entity instanceof ItemEntity itemEntity)) {
            return;
        }

        ItemStack stack = itemEntity.getStack();
        PickupHudTracker.onPickup(stack);
    }
}