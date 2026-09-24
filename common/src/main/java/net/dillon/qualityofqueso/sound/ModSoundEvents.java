package net.dillon.qualityofqueso.sound;

import net.blay09.mods.balm.core.BalmHolderRegistration;
import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.balm.core.BalmRegistrars;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;

/**
 * All sounds for {@code Quality of Queso.}
 */
public class ModSoundEvents {
    private static BalmRegistrar.Scoped<SoundEvent> registrar;

    public static BalmHolderRegistration<SoundEvent> MANAGEMENT_SUCCEED;
    public static BalmHolderRegistration<SoundEvent> MANAGEMENT_REJECT;
    public static BalmHolderRegistration<SoundEvent> MANAGEMENT_DROP;
    public static BalmHolderRegistration<SoundEvent> MANAGEMENT_SORT;
    public static BalmHolderRegistration<SoundEvent> LOCK_SLOT;
    public static BalmHolderRegistration<SoundEvent> UNLOCK_SLOT;

    /**
     * Initializes all sounds.
     */
    public static void registerSounds(BalmRegistrars registrars) {
        registrar = registrars.registrar(Registries.SOUND_EVENT);

        MANAGEMENT_SUCCEED = register("management.succeed");
        MANAGEMENT_REJECT = register("management.reject");
        MANAGEMENT_DROP = register("management.drop");
        MANAGEMENT_SORT = register("management.sort");
        LOCK_SLOT = register("management.lock_slot");
        UNLOCK_SLOT = register("management.unlock_slot");
    }

    /**
     * Registers a sound event.
     */
    private static BalmHolderRegistration<SoundEvent> register(String id) {
        return registrar.register(
                "qualityofqueso."+id,
                SoundEvent::createVariableRangeEvent
        );
    }

    /**
     * @return a sound event.
     */
    public static SoundEvent getSound(BalmHolderRegistration<SoundEvent> sound) {
        return sound.asHolder().value();
    }
}