package net.dillon.qualityofqueso.mixin.client.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static net.dillon.qualityofqueso.util.ModUtil.canApplyEffect;
import static net.dillon.qualityofqueso.util.ModUtil.options;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player {

    public AbstractClientPlayerMixin(Level p_250508_, BlockPos p_250289_, float p_251702_, GameProfile p_252153_) {
        super(p_250508_, p_250289_, p_251702_, p_252153_);
    }

    /**
     * Implements FOV modifiers.
     */
    @Overwrite
    public float getFieldOfViewModifier() {
        float modifier = 1.0F;
        // Disables flying FOV
        if (this.getAbilities().flying && options().fovEffects.flying) {
            modifier *= 1.1F;
        }

        // Potion/sprinting FOV
        float walkingSpeed = this.getAbilities().getWalkingSpeed();
        if (walkingSpeed != 0.0F) {
            float effectiveSpeed = walkingSpeed;

            if (options().fovEffects.sprinting && this.isSprinting()) {
                effectiveSpeed *= 1.3F;
            }

            if (options().fovEffects.potionEffects.enabled()) {
                if (this.hasEffect(MobEffects.MOVEMENT_SPEED)) {
                    MobEffectInstance effect = this.getEffect(MobEffects.MOVEMENT_SPEED);
                    if (effect != null && canApplyEffect(effect)) {
                        effectiveSpeed *= 1.0F + 0.2F * (effect.getAmplifier() + 1);
                    }
                }

                if (this.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                    MobEffectInstance effect = this.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
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
            if (this.getUseItem().getItem() instanceof BowItem && options().fovEffects.bows.enabled()) {
                float scale = options().fovEffects.bows.quickPull() ? 1.0F : Math.min(this.getTicksUsingItem() / 20.0F, 1.0F);
                modifier *= 1.0F - Mth.square(scale) * 0.15F;
            } else if (Minecraft.getInstance().options.getCameraType().isFirstPerson() && this.isScoping()) {
                return 0.1F;
            }
        }

        // Return original FOV
        return Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get().floatValue(), 1.0F, modifier);
    }
}