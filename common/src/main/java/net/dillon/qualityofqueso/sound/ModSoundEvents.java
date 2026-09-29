package net.dillon.qualityofqueso.sound;

import net.blay09.mods.balm.core.BalmHolderRegistration;
import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.balm.core.BalmRegistrars;
import net.dillon.dillonlib.annotation.Dill;
import net.dillon.dillonlib.annotation.DillType;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;

/**
 * All sounds for {@code Quality of Queso.}
 */
@Dill(DillType.CLIENT)
public class ModSoundEvents {
    private static BalmRegistrar.Scoped<SoundEvent> registrar;

    public static BalmHolderRegistration<SoundEvent> ITEMS_MOVED;
    public static BalmHolderRegistration<SoundEvent> ITEM_MOVES_REJECTED;
    public static BalmHolderRegistration<SoundEvent> ITEMS_DROPPED;
    public static BalmHolderRegistration<SoundEvent> ITEMS_SORTED;
    public static BalmHolderRegistration<SoundEvent> SLOT_LOCKED;
    public static BalmHolderRegistration<SoundEvent> SLOT_UNLOCKED;

    /**
     * Initializes all sounds.
     */
    public static void registerSounds(BalmRegistrars registrars) {
        registrar = registrars.registrar(Registries.SOUND_EVENT);

        ITEMS_MOVED = register("management.items_moved");
        ITEM_MOVES_REJECTED = register("management.item_moves_rejected");
        ITEMS_DROPPED = register("management.items_dropped");
        ITEMS_SORTED = register("management.items_sorted");
        SLOT_LOCKED = register("management.slot_locked");
        SLOT_UNLOCKED = register("management.slot_unlocked");
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