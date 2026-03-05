package net.dillon.qualityofqueso.mixin.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.util.ModUtil.modEnabled;
import static net.dillon.qualityofqueso.util.ModUtil.options;

@Mixin(Projectile.class)
public class ProjectileMixin {

    /**
     * Plays the "ding" sound effect when hitting a mob from at least a certain amount of blocks away.
     */
    @Inject(method = "onHitEntity", at = @At("TAIL"))
    private void playHitSoundOnMob(EntityHitResult hitResult, CallbackInfo ci) {
        if (!modEnabled(Minecraft.getInstance()) || !options().mobHitDing) {
            return;
        }

        Entity hitEntity = hitResult.getEntity();

        if (hitEntity instanceof LivingEntity living && !(living instanceof Player)) {
            Projectile projectile = (Projectile) (Object) this;

            Player player = Minecraft.getInstance().player;
            Entity owner = projectile.getOwner();
            if (player == null) {
                return;
            }
            if (!(projectile instanceof Arrow)) {
                return;
            }
            if (owner == null || !owner.getUUID().equals(player.getUUID())) {
                return;
            }

            double minDistance = options().minMobHitDingDistance;
            if (player.distanceToSqr(hitEntity) >= minDistance * minDistance) {
                Level level = living.level();
                if (level instanceof ServerLevel serverLevel) {
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(
                            living.hurtServer(serverLevel,
                                    living.damageSources().arrow((AbstractArrow) projectile, player), 0)
                                    ? SoundEvents.ARROW_HIT_PLAYER
                                    : SoundEvents.SHIELD_BLOCK.value(),
                            1.0F));
                }
            }
        }
    }
}