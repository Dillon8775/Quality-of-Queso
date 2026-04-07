package net.dillon.qualityofqueso.option.instance;

import net.dillon.qualityofqueso.option.base.BaseOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores locked container slots for each container in a world.
 */
public class LockedContainerSlots {
    public Map<String, List<Integer>> lockedContainerSlots = new HashMap<>();

    public static final ModOptionsHandler LOCKED_CONTAINER_SLOTS = new ModOptionsHandler();

    public static class ModOptionsHandler extends BaseOptions<LockedContainerSlots> {

        protected ModOptionsHandler() {
            super(BaseOptions.DEFAULT_TRACKED_CONTAINERS_NAME);
            this.load();
        }

        @Override
        protected LockedContainerSlots createDefault() {
            return new LockedContainerSlots();
        }

        @Override
        protected Class<LockedContainerSlots> getConfigClass() {
            return LockedContainerSlots.class;
        }
    }
}