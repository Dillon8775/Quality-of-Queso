package net.dillon.qualityofqueso.mixin.client.util;

import net.dillon.qualityofqueso.util.ItemHudTracker;
import net.dillon.qualityofqueso.util.MobHitDingTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.helper.ModHelper.modEnabled;
import static net.dillon.qualityofqueso.option.OptionInstances.client;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Shadow
    private ClientLevel level;

    /**
     * Tracks the item that was {@code picked up} to display near the hotbar.
     */
    @Inject(method = "handleTakeItemEntity", at = @At("HEAD"))
    private void trackPickedUpItem(ClientboundTakeItemEntityPacket packet, CallbackInfo ci) {
        if (!modEnabled(Minecraft.getInstance()) || !client().itemCounter().displayOnPickup) {
            return;
        }

        if (packet == null) {
            return;
        }

        if (this.level.getEntity(packet.getPlayerId()) == null || this.level.getEntity(packet.getItemId()) == null) {
            return;
        }

        Entity entity = this.level.getEntity(packet.getItemId());

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        if (this.level.getEntity(packet.getPlayerId()) == null) {
            return;
        }

        try {
            if (this.level.getEntity(packet.getPlayerId()).getId() != player.getId()) {
                return;
            }
        } catch (NullPointerException e) {
            throw new NullPointerException("Unable to send item pickup status because \"ClientLevel.getEntity(int)\" is null.");
        }

        ItemStack stack ;
        if (entity instanceof ItemEntity itemEntity) {
            stack = itemEntity.getItem();
        } else if (entity instanceof AbstractArrow arrow) {
            stack = arrow.getPickupItemStackOrigin();
        } else {
            return;
        }

        if (!stack.isStackable()) {
            return;
        }
        ItemHudTracker.setStack(stack, false);
    }

    /**
     * Queues the {@code mob, hit, ding!} sound effect when hitting a mob from a certain amount of blocks away.
     */
    @Inject(method = "handleDamageEvent", at = @At("HEAD"))
    private void playMobHitDing(ClientboundDamageEventPacket packet, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!modEnabled(minecraft) || !client().misc().mobHitDing || minecraft.player == null) {
            return;
        }

        Entity hitEntity = this.level.getEntity(packet.entityId());
        if (!(hitEntity instanceof LivingEntity)) {
            return;
        }

        DamageSource source = packet.getSource(this.level);
        Entity sourceEntity = source.getEntity();
        Entity directEntity = source.getDirectEntity();
        if (!(directEntity instanceof AbstractArrow) || sourceEntity == null || !sourceEntity.getUUID().equals(minecraft.player.getUUID())) {
            return;
        }

        double minDistance = client().misc().minMobHitDingDistance;
        if (minecraft.player.distanceToSqr(hitEntity) >= minDistance * minDistance) {
            // Evaluate kill state one tick later, after the client receives health/death updates.
            MobHitDingTracker.queueHit(hitEntity.getId(), this.level.getGameTime() + 1L, hitEntity instanceof Player);
        }
    }
}