package net.dillon.qualityofqueso.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dillon.qualityofqueso.main.QoQ.options;

@Mixin(AbstractArrow.class)
public class AbstractArrowMixin {

    /**
     * Plays the "ding" sound effect when hitting a mob from at least a certain amount of blocks away.
     */
    @Inject(method = "onHitEntity", at = @At("TAIL"))
    private void playHitSoundOnMob(EntityHitResult hitResult, CallbackInfo ci) {
        if (!options().mobHitDing) {
            return;
        }

        Entity hitEntity = hitResult.getEntity();

        if (hitEntity instanceof LivingEntity living && !(living instanceof Player)) {
            Player player = Minecraft.getInstance().player;
            if (player == null) {
                return;
            }

            AbstractArrow arrow = (AbstractArrow) (Object) this;
            Entity owner = arrow.getOwner();
            if (owner == null || !owner.getUUID().equals(player.getUUID())) {
                return;
            }

            double minDistance = options().minMobHitDingDistance;
            if (player.distanceToSqr(hitEntity) >= minDistance * minDistance) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ARROW_HIT_PLAYER, 1.0F));
            }
        }
    }
}