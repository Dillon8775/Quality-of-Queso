package net.dillon.qualityofqueso.sound;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import static net.dillon.qualityofqueso.util.ModUtil.ofQoQ;

/**
 * Custom sound events for {@code Quality of Queso.}
 */
public class ModSoundEvents {
    public static SoundEvent MANAGEMENT_SUCCEED = qoqSound("qualityofqueso.management.succeed");
    public static SoundEvent MANAGEMENT_REJECT = qoqSound("qualityofqueso.management.reject");
    public static SoundEvent MANAGEMENT_DROP = qoqSound("qualityofqueso.management.drop");

    private static SoundEvent qoqSound(String id) {
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(ofQoQ(id)));
    }

    public static final ResourceLocation FORTNITE_BATTLE_PASS_ID = ofQoQ("fortnite_battle_pass");
    public static SoundEvent FORTNITE_BATTLE_PASS = SoundEvent.createVariableRangeEvent(FORTNITE_BATTLE_PASS_ID);

    public static void bindSounds(SoundEvent soundEvent) {
        FORTNITE_BATTLE_PASS = soundEvent;
    }

    public static void registerBoundSoundEvents() {
        FORTNITE_BATTLE_PASS = Registry.register(BuiltInRegistries.SOUND_EVENT, FORTNITE_BATTLE_PASS_ID, FORTNITE_BATTLE_PASS);
    }
}