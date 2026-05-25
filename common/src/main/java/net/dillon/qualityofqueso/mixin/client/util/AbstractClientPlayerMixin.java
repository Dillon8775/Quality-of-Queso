package net.dillon.qualityofqueso.mixin.client.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static net.dillon.qualityofqueso.helper.ModHelper.canApplyEffect;
import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player {

    public AbstractClientPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    /**
     * Modifies FOV multipliers, such as sprinting, flying, and potion effects.
     */
    @Overwrite
    public float getFieldOfViewModifier(boolean firstPerson, float effectScale) {
        float modifier = 1.0F;
        // Disables flying FOV
        if (this.getAbilities().flying && clientOptionsInstance().getFovEffectOptions().flying) {
            modifier *= 1.1F;
        }

        // Potion/sprinting FOV
        float walkingSpeed = this.getAbilities().getWalkingSpeed();
        if (walkingSpeed != 0.0F) {
            float effectiveSpeed = walkingSpeed;

            if (clientOptionsInstance().getFovEffectOptions().sprinting > 99 && this.isSprinting()) {
                effectiveSpeed *= (1.3F * ((float) clientOptionsInstance().getFovEffectOptions().sprinting / 100));
            }

            if (clientOptionsInstance().getFovEffectOptions().potions.enabled()) {
                if (this.hasEffect(MobEffects.SPEED)) {
                    MobEffectInstance effect = this.getEffect(MobEffects.SPEED);
                    if (effect != null && canApplyEffect(effect)) {
                        effectiveSpeed *= 1.0F + 0.2F * (effect.getAmplifier() + 1);
                    }
                }

                if (this.hasEffect(MobEffects.SLOWNESS)) {
                    MobEffectInstance effect = this.getEffect(MobEffects.SLOWNESS);
                    if (effect != null && canApplyEffect(effect)) {
                        effectiveSpeed *= 1.0F - 0.15F * (effect.getAmplifier() + 1);
                    }
                }
            }

            float speedFactor = effectiveSpeed / walkingSpeed;
            modifier *= (speedFactor + 1.0F) / 2.0F;
        }

        // Bow FOV
        if (this.isUsingItem()) {
            if (this.getUseItem().getItem() instanceof BowItem && clientOptionsInstance().getFovEffectOptions().bows.enabled()) {
                float scale = clientOptionsInstance().getFovEffectOptions().bows.quickPull() ? 1.0F : Math.min(this.getTicksUsingItem() / 20.0F, 1.0F);
                modifier *= 1.0F - Mth.square(scale) * 0.15F;
            } else if (firstPerson && this.isScoping()) {
                return 0.1F;
            }
        }

        // Return original FOV
        return Mth.lerp(effectScale, 1.0F, modifier);
    }
}