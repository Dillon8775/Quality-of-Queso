package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.util.ModConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores locked player slots, respective to the client-side player.
 */
public class LockedPlayerSlots {
    public static final ModOptionsHandler INSTANCE = new ModOptionsHandler();

    public static class ModOptionsHandler extends BaseOptions<LockedPlayerSlots> {

        protected ModOptionsHandler() {
            super(ModConstants.DEFAULT_LOCKED_PLAYER_SLOTS_FILE_NAME);
            this.load();
        }

        @Override
        protected LockedPlayerSlots createDefault() {
            return new LockedPlayerSlots();
        }

        @Override
        protected Class<LockedPlayerSlots> getConfigClass() {
            return LockedPlayerSlots.class;
        }
    }

    public Map<String, List<Integer>> lockedPlayerSlots = new HashMap<>();
}