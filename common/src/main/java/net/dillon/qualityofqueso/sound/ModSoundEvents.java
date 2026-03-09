package net.dillon.qualityofqueso.sound;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

import static net.dillon.qualityofqueso.util.ModUtil.ofQoQ;

/**
 * Custom sound events for {@code Quality of Queso.}
 */
public class ModSoundEvents {
    public static final Identifier FORTNITE_BATTLE_PASS_ID = ofQoQ("fortnite_battle_pass");
    public static SoundEvent FORTNITE_BATTLE_PASS = SoundEvent.createVariableRangeEvent(FORTNITE_BATTLE_PASS_ID);

    /**
     * Registers a {@code sound event.}
     */
    public static void initializeSoundEvents() {
        FORTNITE_BATTLE_PASS = Registry.register(BuiltInRegistries.SOUND_EVENT, FORTNITE_BATTLE_PASS_ID, FORTNITE_BATTLE_PASS);
    }

    /**
     * Binds NeoForge-registered sound events to the common references.
     */
    public static void bindFortniteBattlePass(SoundEvent soundEvent) {
        FORTNITE_BATTLE_PASS = soundEvent;
    }
}
