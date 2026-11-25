package net.dillon.qualityofqueso.sound;

import net.blay09.mods.balm.core.BalmHolderRegistration;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

/**
 * Sounds for {@link SoundEvents#ARROW_HIT_PLAYER} or other random sounds with custom subtitles.
 */
public enum OtherSoundEvents {
    ELYTRA_ALARM_RINGING(ModSoundEvents.Other.ELYTRA_ALARM_RINGING),
    ARMOR_DING_DINGING(ModSoundEvents.Other.ARMOR_DING_DINGING),
    ALARM_RINGING(ModSoundEvents.Other.ALARM_RINGING),
    ITEM_FRAMES_FOUND(ModSoundEvents.Other.ITEM_FRAMES_FOUND),
    ITEM_FRAMES_GLOW_REMOVED(ModSoundEvents.Other.ITEM_FRAMES_GLOW_REMOVED),
    ITEM_FRAME_SEARCH_FAILED(ModSoundEvents.Other.ITEM_FRAME_SEARCH_FAILED),
    MOB_HIT(ModSoundEvents.Other.MOB_HIT),
    ENTITY_KILLED(ModSoundEvents.Other.ENTITY_KILLED);

    private final BalmHolderRegistration<SoundEvent> sound;

    OtherSoundEvents(final BalmHolderRegistration<SoundEvent> sound) {
        this.sound = sound;
    }

    public BalmHolderRegistration<SoundEvent> sound() {
        return this.sound;
    }
}