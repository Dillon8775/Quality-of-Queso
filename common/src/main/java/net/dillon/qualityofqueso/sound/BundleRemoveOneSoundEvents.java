package net.dillon.qualityofqueso.sound;

import net.blay09.mods.balm.core.BalmHolderRegistration;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

/**
 * Sounds for {@link SoundEvents#BUNDLE_REMOVE_ONE}.
 */
public enum BundleRemoveOneSoundEvents {
    SINGLE_MOVE(ModSoundEvents.BundleRemoveOne.SINGLE_MOVE),
    SORT_MODE_CHANGED(ModSoundEvents.BundleRemoveOne.SORT_MODE_CHANGED),
    FILTER_MODE_CHANGED(ModSoundEvents.BundleRemoveOne.FILTER_MODE_CHANGED),
    ITEMS_MOVED(ModSoundEvents.BundleRemoveOne.ITEMS_MOVED);

    private final BalmHolderRegistration<SoundEvent> sound;

    BundleRemoveOneSoundEvents(final BalmHolderRegistration<SoundEvent> sound) {
        this.sound = sound;
    }

    public BalmHolderRegistration<SoundEvent> sound() {
        return this.sound;
    }
}