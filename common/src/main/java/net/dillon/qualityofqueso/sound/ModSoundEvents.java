package net.dillon.qualityofqueso.sound;

import net.blay09.mods.balm.core.BalmHolderRegistration;
import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.balm.core.BalmRegistrars;
import net.dillon.dillonlib.annotation.Dill;
import net.dillon.dillonlib.annotation.DillEnv;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

/**
 * All sounds for {@code Quality of Queso.}
 */
@Dill(env = DillEnv.COMMON)
public class ModSoundEvents {
    private static BalmRegistrar.Scoped<SoundEvent> commonRegistrar;
    private static BalmRegistrar.Scoped<SoundEvent> clientRegistrar;

    public static BalmHolderRegistration<SoundEvent> SLOT_LOCKED;
    public static BalmHolderRegistration<SoundEvent> SLOT_UNLOCKED;

    /**
     * Registers all common sounds.
     */
    public static void registerCommonSounds(BalmRegistrars registrars) {
        commonRegistrar = registrars.registrar(Registries.SOUND_EVENT);

        Other.ITEM_FRAMES_FOUND = registerCommon("other.item_frames_found");
        Other.ITEM_FRAMES_GLOW_REMOVED = registerCommon("other.item_frames_glow_removed");
        Other.ITEM_FRAME_SEARCH_FAILED = registerCommon("other.item_frame_search_failed");
    }

    /**
     * Registers all client sounds.
     */
    public static void registerClientSounds(BalmRegistrars registrars) {
        clientRegistrar = registrars.registrar(Registries.SOUND_EVENT);

        BundleInsert.PICKUP_FILTER_ITEM = registerClient("bundle_insert.pickup_filter_item");
        BundleInsert.PLACE_FILTER_ITEM = registerClient("bundle_insert.place_filter_item");
        BundleInsert.REMOVE_FILTER_ITEM = registerClient("bundle_insert.remove_filter_item");
        BundleInsert.OPENED_FILTER_SCREEN = registerClient("bundle_insert.opened_filter_screen");
        BundleInsert.CLOSED_FILTER_SCREEN = registerClient("bundle_insert.closed_filter_screen");
        BundleInsert.CONTAINER_FILTERED = registerClient("bundle_insert.container_filtered");
        BundleInsert.CONTAINER_UNFILTERED = registerClient("bundle_insert.container_unfiltered");
        BundleInsert.ITEMS_SWAPPED = registerClient("bundle_insert.items_swapped");
        BundleInsert.ITEMS_SORTED = registerClient("bundle_insert.items_sorted");
        BundleInsert.ITEMS_MOVED = registerClient("bundle_insert.items_moved");

        BundleDropContents.ITEMS_DROPPED = registerClient("bundle_drop_contents.items_dropped");

        BundleInsertFail.MOVE_REJECTED = registerClient("bundle_insert_fail.move_rejected");
        BundleInsertFail.SWAP_REJECTED = registerClient("bundle_insert_fail.swap_rejected");
        BundleInsertFail.SORT_REJECTED = registerClient("bundle_insert_fail.sort_rejected");
        BundleInsertFail.DROP_REJECTED = registerClient("bundle_insert_fail.drop_rejected");

        BundleRemoveOne.SINGLE_MOVE = registerClient("bundle_remove_one.single_move");
        BundleRemoveOne.SORT_MODE_CHANGED = registerClient("bundle_remove_one.sort_mode_changed");
        BundleRemoveOne.FILTER_MODE_CHANGED = registerClient("bundle_remove_one.filter_mode_changed");
        BundleRemoveOne.ITEMS_MOVED = registerClient("bundle_remove_one.items_moved");

        Other.ELYTRA_ALARM_RINGING = registerClient("other.elytra_alarm_ringing");
        Other.ARMOR_DING_DINGING = registerClient("other.armor_ding_dinging");
        Other.ALARM_RINGING = registerClient("other.alarm_ringing");
        Other.MOB_HIT = registerClient("other.mob_hit");
        Other.ENTITY_KILLED = registerClient("other.entity_killed");

        SLOT_LOCKED = registerClient("management.slot_locked");
        SLOT_UNLOCKED = registerClient("management.slot_unlocked");
    }

    /**
     * Registers a common sound event.
     */
    private static BalmHolderRegistration<SoundEvent> registerCommon(String id) {
        return register(commonRegistrar, id);
    }

    /**
     * Registers a client sound event.
     */
    private static BalmHolderRegistration<SoundEvent> registerClient(String id) {
        return register(clientRegistrar, id);
    }

    /**
     * Registers a client sound event.
     */
    private static BalmHolderRegistration<SoundEvent> register(BalmRegistrar.Scoped<SoundEvent> registrar, String id) {
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

    /**
     * Other sounds.
     */
    public static class Other {
        public static BalmHolderRegistration<SoundEvent> ELYTRA_ALARM_RINGING;
        public static BalmHolderRegistration<SoundEvent> ARMOR_DING_DINGING;
        public static BalmHolderRegistration<SoundEvent> ALARM_RINGING;
        public static BalmHolderRegistration<SoundEvent> ITEM_FRAMES_FOUND;
        public static BalmHolderRegistration<SoundEvent> ITEM_FRAMES_GLOW_REMOVED;
        public static BalmHolderRegistration<SoundEvent> ITEM_FRAME_SEARCH_FAILED;
        public static BalmHolderRegistration<SoundEvent> MOB_HIT;
        public static BalmHolderRegistration<SoundEvent> ENTITY_KILLED;
    }
}