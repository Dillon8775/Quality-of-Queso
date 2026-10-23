package net.dillon.qualityofqueso.sound;

import net.blay09.mods.balm.core.BalmHolderRegistration;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

/**
 * Sounds for {@link SoundEvents#BUNDLE_DROP_CONTENTS}.
 */
public enum BundleDropContentsSoundEvents {
    ITEMS_DROPPED(ModSoundEvents.BundleDropContents.ITEMS_DROPPED);

    private final BalmHolderRegistration<SoundEvent> sound;

    BundleDropContentsSoundEvents(final BalmHolderRegistration<SoundEvent> sound) {
        this.sound = sound;
    }

    public BalmHolderRegistration<SoundEvent> sound() {
        return this.sound;
    }
}