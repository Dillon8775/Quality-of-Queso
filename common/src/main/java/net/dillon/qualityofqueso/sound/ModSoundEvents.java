package net.dillon.qualityofqueso.sound;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

import static net.dillon.qualityofqueso.helper.ModHelper.qoqIdentifier;

/**
 * Custom sound events for {@code Quality of Queso.}
 */
public class ModSoundEvents {
    public static SoundEvent MANAGEMENT_SUCCEED = qoqSound("qualityofqueso.management.succeed");
    public static SoundEvent MANAGEMENT_REJECT = qoqSound("qualityofqueso.management.reject");
    public static SoundEvent MANAGEMENT_DROP = qoqSound("qualityofqueso.management.drop");
    public static SoundEvent MANAGEMENT_SORT = qoqSound("qualityofqueso.management.sort");
    public static SoundEvent LOCK_SLOT = qoqSound("qualityofqueso.management.lock_slot");
    public static SoundEvent UNLOCK_SLOT = qoqSound("qualityofqueso.management.unlock_slot");

    /**
     * @return a Quality of Queso sound event.
     */
    private static SoundEvent qoqSound(String id) {
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(qoqIdentifier(id)));
    }

    public static final Identifier FORTNITE_BATTLE_PASS_ID = qoqIdentifier("fortnite_battle_pass");
    public static SoundEvent FORTNITE_BATTLE_PASS = SoundEvent.createVariableRangeEvent(FORTNITE_BATTLE_PASS_ID);

    /**
     * Binds the Fortnite battle pass sound to the sound event.
     */
    public static void bindSounds(SoundEvent soundEvent) {
        FORTNITE_BATTLE_PASS = soundEvent;
    }

    /**
     * Registers the Fortnite battle pass sound at the correct time, so NeoForged doesn't crash.
     */
    public static void registerBoundSoundEvents() {
        FORTNITE_BATTLE_PASS = Registry.register(BuiltInRegistries.SOUND_EVENT, FORTNITE_BATTLE_PASS_ID, FORTNITE_BATTLE_PASS);
    }
}