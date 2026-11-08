package net.dillon.qualityofqueso.helper;

import net.dillon.dillonlib.client.component.SoundBuilder;
import net.dillon.qualityofqueso.container.ContainerSyncer;
import net.dillon.qualityofqueso.sound.*;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import static net.dillon.qualityofqueso.helper.ModConstants.*;
import static net.dillon.qualityofqueso.option.OptionInstances.client;
import static net.dillon.qualityofqueso.sound.ModSoundEvents.getSoundFromRegistration;

/**
 * Helper class for playing sounds.
 */
public class SoundHelper {

    /**
     * Plays the default button press sound.
     */
    public static void playDefaultSound() {
        if (!client().management().playSounds) {
            return;
        }

        SoundBuilder.create(SoundEvents.UI_BUTTON_CLICK.value())
                .buildAndPlay();
    }

    /**
     * Plays a bundle sound.
     */
    private static void playBundleSound(SoundEvent sound) {
        if (!client().management().playSounds) {
            return;
        }

        SoundBuilder.create(sound)
                .volume(5.0F)
                .buildAndPlay();
    }

    /**
     * Plays the {@link SoundEvents#BUNDLE_INSERT} sound.
     */
    public static void playBundleInsertSound(BundleInsertSoundEvents bundleInsertSound) {
        playBundleSound(getSoundFromRegistration(bundleInsertSound.sound()));
    }

    /**
     * Plays the {@link SoundEvents#BUNDLE_DROP_CONTENTS} sound.
     */
    public static void playBundleDropContentsSound(BundleDropContentsSoundEvents bundleDropContentsSound) {
        playBundleSound(getSoundFromRegistration(bundleDropContentsSound.sound()));
    }

    /**
     * Plays the {@link SoundEvents#BUNDLE_REMOVE_ONE} with a cooldown.
     */
    public static void playSafeSortSound(BundleRemoveOneSoundEvents bundleRemoveOneSound) {
        if (SORT_SOUND_COOLDOWN == 0) {
            SORT_SOUND_COOLDOWN = DEFAULT_SORT_SOUND_COOLDOWN;
            playBundleSound(getSoundFromRegistration(bundleRemoveOneSound.sound()));
        }
    }

    /**
     * Plays the disabled bundle sound.
     */
    public static void playButtonInactiveSound(BundleInsertFailSoundEvents bundleInsertFailSound) {
        if (!client().management().playSounds || !ContainerSyncer.CAN_PLAY_INACTIVE_SOUND) {
            ContainerSyncer.CAN_PLAY_INACTIVE_SOUND = true;
            return;
        }

        SoundBuilder.create(getSoundFromRegistration(bundleInsertFailSound.sound()))
                .volume(0.6F)
                .buildAndPlay();
    }

    /**
     * Plays the lock slot sound.
     */
    public static void playLockSlotSound(boolean lock) {
        if (!client().lockedSlots().lockedSlots || !client().lockedSlots().lockSound || LOCKED_SLOT_SOUND_COOLDOWN > 0) {
            return;
        }

        SoundBuilder.create(lock ? getSoundFromRegistration(ModSoundEvents.SLOT_LOCKED) : getSoundFromRegistration(ModSoundEvents.SLOT_UNLOCKED))
                .volume(0.10F)
                .buildAndPlay();
        LOCKED_SLOT_SOUND_COOLDOWN = DEFAULT_LOCKED_SLOT_SOUND_COOLDOWN;
    }

    /**
     * Plays the quick equip sound.
     */
    public static void playQuickEquipSound() {
        SoundBuilder.create(SoundEvents.ARMOR_EQUIP_GENERIC.value())
                .volume(0.45F)
                .buildAndPlay();
    }

    /**
     * Plays the open sound for this button.
     */
    public static void playEnderChestOpenSound() {
        SoundBuilder.create(SoundEvents.ENDER_CHEST_OPEN).buildAndPlay();
    }

    /**
     * Plays the close sound for this button.
     */
    public static void playEnderChestCloseSound() {
        SoundBuilder.create(SoundEvents.ENDER_CHEST_CLOSE).buildAndPlay();
    }
}