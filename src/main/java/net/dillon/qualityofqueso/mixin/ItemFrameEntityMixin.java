package net.dillon.qualityofqueso.mixin;

import net.dillon.qualityofqueso.util.GlowCountdown;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Tracks glow countdown and removes the glow effect when the countdown reaches 0.
 * <p>We do it like this so the timer resumes even when the chunk becomes unloaded or the player leaves and rejoins the world.</p>
 */
@Mixin(ItemFrameEntity.class)
public abstract class ItemFrameEntityMixin extends AbstractDecorationEntity implements GlowCountdown {
    @Unique
    private int glowTicksRemaining = -1;

    public ItemFrameEntityMixin(EntityType<? extends AbstractDecorationEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        // Decrement timer
        if (this.glowTicksRemaining > 0) {
            this.glowTicksRemaining--;

            // Once timer reaches 0, turn off glow effect
            if (this.glowTicksRemaining == 0) {
                ((ItemFrameEntity)(Object)this).setGlowing(false);
            }
        }
        super.tick();
    }

    /**
     * Sets the glow ticks remaining.
     */
    @Unique
    public void startGlowCountdown(int ticks) {
        this.glowTicksRemaining = ticks;
    }

    /**
     * Writes the glow ticks remaining to NBT.
     */
    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeGlowTicks(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("GlowTicksRemaining", this.glowTicksRemaining);
    }

    /**
     * Reads the glow ticks remaining to NBT.
     */
    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readGlowTicks(NbtCompound nbt, CallbackInfo ci) {
        this.glowTicksRemaining = nbt.getInt("GlowTicksRemaining");
    }
}