package net.dillon.qualityofqueso.sound;

import net.blay09.mods.balm.core.BalmHolderRegistration;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

/**
 * Sounds for {@link SoundEvents#BUNDLE_INSERT_FAIL}.
 */
public enum BundleInsertFailSoundEvents {
    MOVE_REJECTED(ModSoundEvents.BundleInsertFail.MOVE_REJECTED),
    SWAP_REJECTED(ModSoundEvents.BundleInsertFail.SWAP_REJECTED),
    SORT_REJECTED(ModSoundEvents.BundleInsertFail.SORT_REJECTED),
    DROP_REJECTED(ModSoundEvents.BundleInsertFail.DROP_REJECTED);

    private final BalmHolderRegistration<SoundEvent> sound;

    BundleInsertFailSoundEvents(final BalmHolderRegistration<SoundEvent> sound) {
        this.sound = sound;
    }

    public BalmHolderRegistration<SoundEvent> sound() {
        return this.sound;
    }
}