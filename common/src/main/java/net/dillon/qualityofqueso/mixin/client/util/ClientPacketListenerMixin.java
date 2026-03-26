package net.dillon.qualityofqueso.mixin.client.util;

import net.dillon.qualityofqueso.util.ItemHudTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.sounds.SoundEvents;
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
        if (!modEnabled(Minecraft.getInstance()) || !options().hud.displayOnPickup) {
            return;
        }

        if (packet == null) {
            return;
        }

        if (this.level.getEntity(packet.getPlayerId()) == null || this.level.getEntity(packet.getItemId()) == null) {
            return;
        }

        Entity entity = this.level.getEntity(packet.getItemId());
        if (!(entity instanceof ItemEntity itemEntity)) {
            return;
        }

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

        ItemStack stack = itemEntity.getItem();
        ItemHudTracker.setStack(stack);
    }

    /**
     * Plays the "ding" sound effect when hitting a mob from at least a certain amount of blocks away.
     */
    @Inject(method = "handleDamageEvent", at = @At("TAIL"))
    private void playHitSoundOnMob(ClientboundDamageEventPacket packet, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!modEnabled(minecraft) || !options().misc.mobHitDing || minecraft.player == null) {
            return;
        }

        Entity hitEntity = this.level.getEntity(packet.entityId());
        if (!(hitEntity instanceof LivingEntity living) || living instanceof Player) {
            return;
        }

        DamageSource source = packet.getSource(this.level);
        Entity sourceEntity = source.getEntity();
        Entity directEntity = source.getDirectEntity();
        if (!(directEntity instanceof AbstractArrow) || sourceEntity == null || !sourceEntity.getUUID().equals(minecraft.player.getUUID())) {
            return;
        }

        double minDistance = options().misc.minMobHitDingDistance;
        if (minecraft.player.distanceToSqr(hitEntity) >= minDistance * minDistance) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ARROW_HIT_PLAYER, 1.0F));
        }
    }
}