package net.dillon.qualityofqueso.mixin.main;

import net.dillon.qualityofqueso.server.DedicatedServerStorage;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.helper.ModHelper.commonOptionsInstance;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

    /**
     * Blocks automatic pickup while inventory locking is enabled unless this pickup was explicitly requested.
     */
    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    private void blockAutomaticPickupWhenLocked(Player player, CallbackInfo ci) {
        if (!commonOptionsInstance().inventoryLocking || !DedicatedServerStorage.isLockedInventory(player.getUUID())) {
            return;
        }

        ItemEntity self = (ItemEntity) (Object) this;

        if (DedicatedServerStorage.isSoftLockedInventory(player.getUUID()) && player.getInventory().contains(self.getItem())) {
            return;
        }

        long currentTick = player.level().getGameTime();
        if (!DedicatedServerStorage.consumeManualPickup(player.getUUID(), self.getId(), currentTick)) {
            ci.cancel();
        }
    }
}