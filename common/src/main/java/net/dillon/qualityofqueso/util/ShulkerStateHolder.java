package net.dillon.qualityofqueso.util;

import net.dillon.dillonlib.annotation.Dill;
import net.dillon.dillonlib.annotation.DillType;

import java.util.List;

/**
 * Stores per-shulker QoQ state on the shulker block entity itself.
 */
@Dill(DillType.COMMON)
public interface ShulkerStateHolder {

    /**
     * @return saved sorting mode name.
     */
    String getSortingMode();

    /**
     * Sets saved sorting mode name.
     */
    void setSortingMode(String sortingMode);

    /**
     * @return the filtering mode for a shulker box.
     */
    String getFilteringMode();

    /**
     * Sets the filtering mode for a shulker box.
     */
    void setFilteringMode(String filteringMode);

    /**
     * @return whether this shulker uses tag filtering mode.
     */
    boolean isTagFiltered();

    /**
     * Sets if the shulker box should use tag filtering.
     */
    void setTagFiltered(boolean tagFiltered);

    /**
     * @return saved filter placeholder item identifiers.
     */
    List<String> getFilterItems();

    /**
     * Replaces saved filter placeholder item identifiers.
     *
     * @param filterItems item identifiers to persist.
     */
    void setFilterItems(List<String> filterItems);

    /**
     * @return saved locked slot indices.
     */
    List<Integer> getLockedSlots();

    /**
     * Replaces saved locked slot indices.
     *
     * @param lockedSlots slot indices to persist.
     */
    void setLockedSlots(List<Integer> lockedSlots);
}