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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.dillon.qualityofqueso.helper.ModHelper.canApplyEffect;
import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player {

    public AbstractClientPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    /**
     * Injects the custom fov modifications, with the custom option and original return values for vanilla gameplay.
     */
    @Inject(method = "getFieldOfViewModifier", at = @At("RETURN"), cancellable = true)
    private void applyCustomFovEffects(boolean firstPerson, float effectScale, CallbackInfoReturnable<Float> cir) {
        float original = cir.getReturnValueF();
        float vanillaModifier = this.computeModifier(firstPerson, false);
        float customModifier = this.computeModifier(firstPerson, true);

        float vanilla = Mth.lerp(effectScale, 1.0F, vanillaModifier);
        float custom = Mth.lerp(effectScale, 1.0F, customModifier);

        if (Math.abs(vanilla) < 1.0E-6F) {
            cir.setReturnValue(custom);
            return;
        }

        // Preserve changes from other mods by scaling the existing return value
        cir.setReturnValue(original * (custom / vanilla));
    }

    /**
     * Computes the fov multiplier to use, with original return values and options taken into account.
     */
    @Unique
    private float computeModifier(boolean firstPerson, boolean useOptions) {
        float modifier = 1.0F;

        if (this.getAbilities().flying && (!useOptions || clientOptionsInstance().getFovEffectOptions().flying)) {
            modifier *= 1.1F;
        }

        float walkingSpeed = this.getAbilities().getWalkingSpeed();
        if (walkingSpeed != 0.0F) {
            float effectiveSpeed = walkingSpeed;

            if (this.isSprinting()) {
                if (useOptions) {
                    if (clientOptionsInstance().getFovEffectOptions().sprinting > 99) {
                        effectiveSpeed *= (1.3F * ((float) clientOptionsInstance().getFovEffectOptions().sprinting / 100));
                    }
                } else {
                    effectiveSpeed *= 1.3F;
                }
            }

            if (!useOptions || clientOptionsInstance().getFovEffectOptions().potions.enabled()) {
                if (this.hasEffect(MobEffects.SPEED)) {
                    MobEffectInstance effect = this.getEffect(MobEffects.SPEED);
                    if (effect != null && (!useOptions || canApplyEffect(effect))) {
                        effectiveSpeed *= 1.0F + 0.2F * (effect.getAmplifier() + 1);
                    }
                }

                if (this.hasEffect(MobEffects.SLOWNESS)) {
                    MobEffectInstance effect = this.getEffect(MobEffects.SLOWNESS);
                    if (effect != null && (!useOptions || canApplyEffect(effect))) {
                        effectiveSpeed *= 1.0F - 0.15F * (effect.getAmplifier() + 1);
                    }
                }
            }

            float speedFactor = effectiveSpeed / walkingSpeed;
            modifier *= (speedFactor + 1.0F) / 2.0F;
        }

        if (this.isUsingItem()) {
            if (this.getUseItem().getItem() instanceof BowItem) {
                if (!useOptions || clientOptionsInstance().getFovEffectOptions().bows.enabled()) {
                    float scale = useOptions && clientOptionsInstance().getFovEffectOptions().bows.quickPull()
                            ? 1.0F
                            : Math.min(this.getTicksUsingItem() / 20.0F, 1.0F);
                    modifier *= 1.0F - Mth.square(scale) * 0.15F;
                }
            } else if (firstPerson && this.isScoping()) {
                return 0.1F;
            }
        }

        return modifier;
    }
}