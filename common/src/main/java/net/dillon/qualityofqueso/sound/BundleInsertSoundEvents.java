package net.dillon.qualityofqueso.sound;

import net.blay09.mods.balm.core.BalmHolderRegistration;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

/**
 * Sounds for {@link SoundEvents#BUNDLE_INSERT}.
 */
public enum BundleInsertSoundEvents {
    PICKUP_FILTER_ITEM(ModSoundEvents.BundleInsert.PICKUP_FILTER_ITEM),
    PLACE_FILTER_ITEM(ModSoundEvents.BundleInsert.PLACE_FILTER_ITEM),
    REMOVE_FILTER_ITEM(ModSoundEvents.BundleInsert.REMOVE_FILTER_ITEM),
    OPENED_FILTER_SCREEN(ModSoundEvents.BundleInsert.OPENED_FILTER_SCREEN),
    CLOSED_FILTER_SCREEN(ModSoundEvents.BundleInsert.CLOSED_FILTER_SCREEN),
    CONTAINER_FILTERED(ModSoundEvents.BundleInsert.CONTAINER_FILTERED),
    CONTAINER_UNFILTERED(ModSoundEvents.BundleInsert.CONTAINER_UNFILTERED),
    ITEMS_SWAPPED(ModSoundEvents.BundleInsert.ITEMS_SWAPPED),
    ITEMS_SORTED(ModSoundEvents.BundleInsert.ITEMS_SORTED),
    ITEMS_MOVED(ModSoundEvents.BundleInsert.ITEMS_MOVED);

    private final BalmHolderRegistration<SoundEvent> sound;

    BundleInsertSoundEvents(final BalmHolderRegistration<SoundEvent> sound) {
        this.sound = sound;
    }

    public BalmHolderRegistration<SoundEvent> sound() {
        return this.sound;
    }
}