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
     * @return whether this shulker has QoQ filtering enabled.
     */
    boolean isFiltered();

    /**
     * Sets whether this shulker has QoQ filtering enabled.
     *
     * @param filtered {@code true} to enable filtering.
     */
    void setFiltered(boolean filtered);

    /**
     * @return whether this shulker uses tag filtering mode.
     */
    boolean isTagFiltered();

    /**
     * Sets the shulker filter mode.
     *
     * @param tagFiltered {@code true} for tag mode, {@code false} for item mode.
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

    /**
     * @return saved sorting mode name.
     */
    String getSortingMode();

    /**
     * Sets saved sorting mode name.
     *
     * @param sortingMode sorting mode enum name to persist.
     */
    void setSortingMode(String sortingMode);
}