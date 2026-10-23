package net.dillon.qualityofqueso.container.data;

import java.util.ArrayList;
import java.util.List;

import static net.dillon.qualityofqueso.container.ContainerSyncer.getDefaultSortingModeForContainer;
import static net.dillon.qualityofqueso.option.eum.management.FilteringMode.unfiltered;

/**
     * Runtime client cache for the currently known state of one shulker position.
     */
public class ShulkerState {
    public String sortingMode = getDefaultSortingModeForContainer().name();
    public String filteringMode = unfiltered();
    public boolean tagFiltered;
    public boolean locked;
    public List<String> filterItems = new ArrayList<>();
    public List<Integer> lockedSlots = new ArrayList<>();
}