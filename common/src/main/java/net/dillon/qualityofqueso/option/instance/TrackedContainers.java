package net.dillon.qualityofqueso.option.instance;

import net.dillon.qualityofqueso.option.base.BaseOptions;

import java.util.*;

/**
 * Stores tracked containers in a separate file.
 */
public class TrackedContainers {
    public Set<String> trackedFilteredByItemContainers = new HashSet<>();
    public Set<String> trackedFilteredByTagContainers = new HashSet<>();
    public Map<String, List<String>> trackedFilteredItems = new HashMap<>();

    public static final ModOptionsHandler TRACKED_CONTAINERS = new ModOptionsHandler();

    public static class ModOptionsHandler extends BaseOptions<TrackedContainers> {

        protected ModOptionsHandler() {
            super(BaseOptions.DEFAULT_TRACKED_CONTAINERS_NAME);
            this.load();
        }

        @Override
        protected TrackedContainers createDefault() {
            return new TrackedContainers();
        }

        @Override
        protected Class<TrackedContainers> getConfigClass() {
            return TrackedContainers.class;
        }
    }
}
