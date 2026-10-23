package net.dillon.qualityofqueso.sound;

import net.blay09.mods.balm.core.BalmHolderRegistration;
import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.balm.core.BalmRegistrars;
import net.dillon.dillonlib.annotation.Dill;
import net.dillon.dillonlib.annotation.DillType;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

/**
 * All sounds for {@code Quality of Queso.}
 */
@Dill(DillType.CLIENT)
public class ModSoundEvents {
    private static BalmRegistrar.Scoped<SoundEvent> registrar;

    public static BalmHolderRegistration<SoundEvent> SLOT_LOCKED;
    public static BalmHolderRegistration<SoundEvent> SLOT_UNLOCKED;

    /**
     * Initializes all sounds.
     */
    public static void registerSounds(BalmRegistrars registrars) {
        registrar = registrars.registrar(Registries.SOUND_EVENT);

        BundleInsert.PICKUP_FILTER_ITEM = register("bundle_insert.pickup_filter_item");
        BundleInsert.PLACE_FILTER_ITEM = register("bundle_insert.place_filter_item");
        BundleInsert.REMOVE_FILTER_ITEM = register("bundle_insert.remove_filter_item");
        BundleInsert.OPENED_FILTER_SCREEN = register("bundle_insert.opened_filter_screen");
        BundleInsert.CLOSED_FILTER_SCREEN = register("bundle_insert.closed_filter_screen");
        BundleInsert.CONTAINER_FILTERED = register("bundle_insert.container_filtered");
        BundleInsert.CONTAINER_UNFILTERED = register("bundle_insert.container_unfiltered");
        BundleInsert.ITEMS_SWAPPED = register("bundle_insert.items_swapped");
        BundleInsert.ITEMS_SORTED = register("bundle_insert.items_sorted");
        BundleInsert.ITEMS_MOVED = register("bundle_insert.items_moved");

        BundleDropContents.ITEMS_DROPPED = register("bundle_drop_contents.items_dropped");

        BundleInsertFail.MOVE_REJECTED = register("bundle_insert_fail.move_rejected");
        BundleInsertFail.SWAP_REJECTED = register("bundle_insert_fail.swap_rejected");
        BundleInsertFail.SORT_REJECTED = register("bundle_insert_fail.sort_rejected");
        BundleInsertFail.DROP_REJECTED = register("bundle_insert_fail.drop_rejected");

        BundleRemoveOne.SINGLE_MOVE = register("bundle_remove_one.single_move");
        BundleRemoveOne.SORT_MODE_CHANGED = register("bundle_remove_one.sort_mode_changed");
        BundleRemoveOne.FILTER_MODE_CHANGED = register("bundle_remove_one.filter_mode_changed");
        BundleRemoveOne.ITEMS_MOVED = register("bundle_remove_one.items_moved");

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
    public static SoundEvent getSoundFromRegistration(BalmHolderRegistration<SoundEvent> sound) {
        return sound.asHolder().value();
    }

    /**
     * Item moved specific sounds (associated with {@link SoundEvents#BUNDLE_INSERT}.
     */
    public static class BundleInsert {
        public static BalmHolderRegistration<SoundEvent> PICKUP_FILTER_ITEM;
        public static BalmHolderRegistration<SoundEvent> PLACE_FILTER_ITEM;
        public static BalmHolderRegistration<SoundEvent> REMOVE_FILTER_ITEM;
        public static BalmHolderRegistration<SoundEvent> OPENED_FILTER_SCREEN;
        public static BalmHolderRegistration<SoundEvent> CLOSED_FILTER_SCREEN;
        public static BalmHolderRegistration<SoundEvent> CONTAINER_FILTERED;
        public static BalmHolderRegistration<SoundEvent> CONTAINER_UNFILTERED;
        public static BalmHolderRegistration<SoundEvent> ITEMS_SWAPPED;
        public static BalmHolderRegistration<SoundEvent> ITEMS_SORTED;
        public static BalmHolderRegistration<SoundEvent> ITEMS_MOVED;
    }

    /**
     * Drop-specific sounds (associated with {@link SoundEvents#BUNDLE_DROP_CONTENTS}.
     */
    public static class BundleDropContents {
        public static BalmHolderRegistration<SoundEvent> ITEMS_DROPPED;
    }

    /**
     * Rejected-specific sounds (associated with {@link SoundEvents#BUNDLE_INSERT_FAIL}.
     */
    public static class BundleInsertFail {
        public static BalmHolderRegistration<SoundEvent> MOVE_REJECTED;
        public static BalmHolderRegistration<SoundEvent> SWAP_REJECTED;
        public static BalmHolderRegistration<SoundEvent> SORT_REJECTED;
        public static BalmHolderRegistration<SoundEvent> DROP_REJECTED;
    }

    /**
     * Sort-specific sounds (associated with {@link SoundEvents#BUNDLE_REMOVE_ONE}.
     */
    public static class BundleRemoveOne {
        public static BalmHolderRegistration<SoundEvent> SINGLE_MOVE;
        public static BalmHolderRegistration<SoundEvent> SORT_MODE_CHANGED;
        public static BalmHolderRegistration<SoundEvent> FILTER_MODE_CHANGED;
        public static BalmHolderRegistration<SoundEvent> ITEMS_MOVED;
    }
}