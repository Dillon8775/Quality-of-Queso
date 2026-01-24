package net.dillon.qualityofqueso.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.main.QoQ.options;

@Mixin(ProjectileEntity.class)
public class ProjectileEntityMixin {

    /**
     * Plays the "ding" sound effect when hitting a mob from at least a certain amount of blocks away.
     */
    @Inject(method = "onEntityHit", at = @At("TAIL"))
    private void playHitSoundOnMob(EntityHitResult hitResult, CallbackInfo ci) {
        if (!options().mobHitDing) {
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
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_ARROW_HIT_PLAYER, 1.0F));
            }
        }
    }
}