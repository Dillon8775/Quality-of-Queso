package net.dillon.qualityofqueso.mixin.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.main.QoQ.modEnabled;
import static net.dillon.qualityofqueso.main.QoQ.options;

@Mixin(ProjectileEntity.class)
public class ProjectileEntityMixin {

    /**
     * Plays the "ding" sound effect when hitting a mob from at least a certain amount of blocks away.
     */
    @Inject(method = "onEntityHit", at = @At("TAIL"))
    private void playHitSoundOnMob(EntityHitResult hitResult, CallbackInfo ci) {
        if (!modEnabled(MinecraftClient.getInstance()) || !options().mobHitDing) {
            return;
        }

        Entity hitEntity = hitResult.getEntity();

        if (hitEntity instanceof LivingEntity living && !(living instanceof PlayerEntity)) {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) {
                return;
            }

            double minDistance = options().minMobHitDingDistance;
            ProjectileEntity projectile = (ProjectileEntity) (Object) this;
            if (!projectile.isOwner(player)) {
                return;
            }

            if (player.squaredDistanceTo(hitEntity) >= minDistance * minDistance) {
                World world = living.getEntityWorld();
                if (world instanceof ServerWorld serverWorld) {
                    MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(
                            living.damage(serverWorld,
                                    living.getDamageSources().arrow((PersistentProjectileEntity) projectile, player), 0)
                                    ? SoundEvents.ENTITY_ARROW_HIT_PLAYER
                                    : SoundEvents.ITEM_SHIELD_BLOCK.value(),
                            1.0F));
                }
            }
        }
    }
}