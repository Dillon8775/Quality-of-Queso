package net.dillon.qualityofqueso.mixin.main;

import net.dillon.dillonlib.annotation.Dill;
import net.dillon.dillonlib.annotation.DillType;
import net.dillon.qualityofqueso.util.GlowCountdown;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Tracks glow countdown and removes the glow effect when the countdown reaches 0.
 * <p>We do it like this so the timer resumes even when the chunk becomes unloaded or the player leaves and rejoins the world.</p>
 */
@Dill(DillType.COMMON)
@Mixin(ItemFrame.class)
public abstract class ItemFrameMixin extends HangingEntity implements GlowCountdown {
    @Unique
    private int glowTicksRemaining = -1;

    public ItemFrameMixin(EntityType<? extends HangingEntity> p_31703_, Level p_31704_) {
        super(p_31703_, p_31704_);
    }

    /**
     * Sets the glow ticks remaining.
     */
    @Override
    public void startGlowCountdown(int ticks) {
        this.glowTicksRemaining = ticks;
    }

    /**
     * Writes the glow ticks remaining to NBT.
     */
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeGlowTicks(ValueOutput output, CallbackInfo ci) {
        output.putInt("GlowTicksRemaining", this.glowTicksRemaining);
    }

    /**
     * Reads the glow ticks remaining to NBT.
     */
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readGlowTicks(ValueInput input, CallbackInfo ci) {
        this.glowTicksRemaining = input.getIntOr("GlowTicksRemaining", -1);
    }

    @Override
    public void tick() {
        // Decrement timer
        if (this.glowTicksRemaining > 0) {
            this.glowTicksRemaining--;

            // Once timer reaches 0, turn off glow effect
            if (this.glowTicksRemaining == 0) {
                ((ItemFrame) (Object) this).setGlowingTag(false);
            }
        }
        super.tick();
    }
}