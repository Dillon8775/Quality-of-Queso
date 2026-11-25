package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.container.data.LockedContainerSlotData;
import net.dillon.qualityofqueso.helper.ModConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.dillon.dillonlib.util.BaseOptions.guardedInstance;

/**
 * Stores locked container slots for each container in a world.
 */
public class LockedContainerSlots {
    private static final LockedContainerSlotsHandler INSTANCE = new LockedContainerSlotsHandler();
    public Map<String, List<LockedContainerSlotData>> lockedContainerSlots = new HashMap<>();

    public static LockedContainerSlotsHandler getInstance() {
        return guardedInstance(INSTANCE);
    }

    public static class LockedContainerSlotsHandler extends ModBaseOptionsHandler<LockedContainerSlots> {

        protected LockedContainerSlotsHandler() {
            super(ModConstants.DEFAULT_LOCKED_CONTAINER_SLOTS_FILE_NAME);
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