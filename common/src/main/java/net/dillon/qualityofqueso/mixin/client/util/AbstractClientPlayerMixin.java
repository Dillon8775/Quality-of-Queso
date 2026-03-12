package net.dillon.qualityofqueso.mixin.client.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static net.dillon.qualityofqueso.util.ModUtil.modEnabled;
import static net.dillon.qualityofqueso.util.ModUtil.options;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player {

    public AbstractClientPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    /**
     * Removes the FOV modification when flying.
     */
    @ModifyConstant(method = "getFieldOfViewModifier", constant = @Constant(floatValue = 1.1F))
    private float removeFlyingSpeedFOVMultiplier(float original) {
        if (modEnabled(Minecraft.getInstance())) {
            return !options().fovEffects.flying ? 1.0F : original;
        }
        return original;
    }

    /**
     * Removes the FOV modification when sprinting.
     */
    @ModifyVariable(method = "getFieldOfViewModifier", at = @At("STORE"), ordinal = 1)
    private float removeSprintingFOVMultiplier(float original) {
        if (modEnabled(Minecraft.getInstance())) {
            boolean bl = options().fovEffects.flying && this.getAbilities().flying;
            float f = bl ? original : 1.0F;
            return !options().fovEffects.sprinting ? bl ? 1.1F : f : original;
        }
        return original;
    }

    /**
     * Removes the FOV change when you have a potion effect applied.
     */
    @Inject(method = "getFieldOfViewModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Abilities;getWalkingSpeed()F"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    private void modifyPotionFOVModifier(boolean firstPerson, float effectScale, CallbackInfoReturnable<Float> cir, float modifier) {
        if (modEnabled(Minecraft.getInstance()) && !(this.getUseItem().getItem() instanceof BowItem) && !options().fovEffects.potions) {
            float f = options().fovEffects.sprinting && this.isSprinting() ? 1.02F + (effectScale / 10.0F) : 1.0F;
            cir.setReturnValue(Math.min(Math.max(effectScale, f), f));
        }
    }

    /**
     * Modifies bow FOV multiplier.
     */
    @Inject(method = "getFieldOfViewModifier", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(FF)F"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    private void modifyBowFOVMultiplier(boolean firstPerson, float effectScale, CallbackInfoReturnable<Float> cir, float modifier) {
        if (!modEnabled(Minecraft.getInstance())) {
            return;
        }

        if (options().fovEffects.bows.on() && !options().fovEffects.potions) {
            cir.setReturnValue(1.0F - Mth.square(Math.min(this.getTicksUsingItem() / 20.0F, 1.0F)) * 0.15F);
        }

        if (options().fovEffects.bows.on()) {
            return;
        }

        if (options().fovEffects.bows.isQuick()) {
            cir.setReturnValue((!options().fovEffects.potions ? 1.0F : modifier) * (1.0F - Mth.square(1.0F) * 0.15F));
        } else {
            cir.setReturnValue(1.0F);
        }
    }
}