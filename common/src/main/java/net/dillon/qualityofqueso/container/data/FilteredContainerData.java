package net.dillon.qualityofqueso.container.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores data for filtered containers with a specific filter mode.
 */
public class FilteredContainerData {
    public String filteringType = "";
    public String filteringMode = "";
    public List<String> filterItems = new ArrayList<>();
    public FilteredContainer container = new FilteredContainer();
}