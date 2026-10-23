package net.dillon.qualityofqueso.helper;

import net.dillon.qualityofqueso.container.ContainerSyncer;
import net.dillon.qualityofqueso.sound.*;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import static net.dillon.dillonlib.task.ClientTasks.getMinecraft;
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

        getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    /**
     * Plays a sound at a specified volume.
     */
    private static void playSound(SoundEvent sound) {
        if (!client().management().playSounds) {
            return;
        }

        getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(sound, 1.0F, 5.0F));
    }

    /**
     * Plays the {@link SoundEvents#BUNDLE_INSERT} sound.
     */
    public static void playBundleInsertSound(BundleInsertSoundEvents bundleInsertSound) {
        playSound(getSoundFromRegistration(bundleInsertSound.sound()));
    }

    /**
     * Plays the {@link SoundEvents#BUNDLE_DROP_CONTENTS} sound.
     */
    public static void playBundleDropContentsSound(BundleDropContentsSoundEvents bundleDropContentsSound) {
        playSound(getSoundFromRegistration(bundleDropContentsSound.sound()));
    }

    /**
     * Plays the {@link SoundEvents#BUNDLE_REMOVE_ONE} with a cooldown.
     */
    public static void playSafeSortSound(BundleRemoveOneSoundEvents bundleRemoveOneSound) {
        if (SORT_SOUND_COOLDOWN == 0) {
            SORT_SOUND_COOLDOWN = DEFAULT_SORT_SOUND_COOLDOWN;
            playSound(getSoundFromRegistration(bundleRemoveOneSound.sound()));
        }
    }

    /**
     * Plays the inactive bundle sound.
     */
    public static void playButtonInactiveSound(BundleInsertFailSoundEvents bundleInsertFailSound) {
        if (!client().management().playSounds || !ContainerSyncer.CAN_PLAY_INACTIVE_SOUND) {
            ContainerSyncer.CAN_PLAY_INACTIVE_SOUND = true;
            return;
        }

        getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(getSoundFromRegistration(bundleInsertFailSound.sound()), 1.0F, 0.6F));
    }

    /**
     * Plays the lock slot sound.
     */
    public static void playLockSlotSound(boolean lock) {
        if (!client().lockedSlots().lockedSlots || !client().lockedSlots().lockSound || LOCKED_SLOT_SOUND_COOLDOWN > 0) {
            return;
        }

        getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(lock ? getSoundFromRegistration(ModSoundEvents.SLOT_LOCKED) : getSoundFromRegistration(ModSoundEvents.SLOT_UNLOCKED), 1.0F, 0.10F));
        LOCKED_SLOT_SOUND_COOLDOWN = DEFAULT_LOCKED_SLOT_SOUND_COOLDOWN;
    }

    /**
     * Plays the quick equip sound.
     */
    public static void playQuickEquipSound() {
        getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ARMOR_EQUIP_GENERIC.value(), 1.0F, 0.45F));
    }
}