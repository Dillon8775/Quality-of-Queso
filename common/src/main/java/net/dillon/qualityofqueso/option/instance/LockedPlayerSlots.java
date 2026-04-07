package net.dillon.qualityofqueso.option.instance;

import net.dillon.qualityofqueso.option.base.BaseOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores locked player slots, respective to the client-side player.
 */
public class LockedPlayerSlots {
    public Map<String, List<Integer>> lockedPlayerSlots = new HashMap<>();

    public static final ModOptionsHandler LOCKED_PLAYER_SLOTS = new ModOptionsHandler();

    public static class ModOptionsHandler extends BaseOptions<LockedPlayerSlots> {

        protected ModOptionsHandler() {
            super(BaseOptions.DEFAULT_TRACKED_CONTAINERS_NAME);
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
}