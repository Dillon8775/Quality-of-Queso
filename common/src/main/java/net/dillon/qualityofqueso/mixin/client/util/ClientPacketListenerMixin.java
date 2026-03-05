package net.dillon.qualityofqueso.mixin.client.util;

import net.dillon.qualityofqueso.util.PickupHudTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.util.ModUtil.modEnabled;
import static net.dillon.qualityofqueso.util.ModUtil.options;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Shadow
    private ClientLevel level;

    /**
     * Tracks the item that was {@code picked up} to display near the hotbar.
     */
    @Inject(method = "handleTakeItemEntity", at = @At("HEAD"))
    private void trackPickedUpItem(ClientboundTakeItemEntityPacket packet, CallbackInfo ci) {
        if (!modEnabled(Minecraft.getInstance()) || !options().displayOnPickup) {
            return;
        }

        Entity entity = this.level.getEntity(packet.getItemId());
        if (!(entity instanceof ItemEntity itemEntity)) {
            return;
        }

        ItemStack stack = itemEntity.getItem();
        PickupHudTracker.setStack(stack);
    }
}