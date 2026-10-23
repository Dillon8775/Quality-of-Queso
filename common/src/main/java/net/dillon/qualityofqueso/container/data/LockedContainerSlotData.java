package net.dillon.qualityofqueso.container.data;

import java.util.HashSet;
import java.util.Set;

/**
 * Stores locked slots per container.
 */
public class LockedContainerSlotData {
    public Set<Integer> slots = new HashSet<>();
    public FilteredContainer container = new FilteredContainer();
}