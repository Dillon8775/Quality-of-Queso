package net.dillon.qualityofqueso.container;

import net.dillon.dillonlib.annotation.Dill;
import net.dillon.dillonlib.annotation.DillType;
import net.dillon.qualityofqueso.container.data.ShulkerState;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;

import java.util.List;

/**
 * Stores per-shulker state on the shulker block entity itself.
 */
@Dill(DillType.COMMON)
public interface ShulkerBlockEntityHolder {

    /**
     * Assigns all values to a {@link ShulkerState}.
     */
    static void writeShulkerState(
            ShulkerState state,
            String sortingMode,
            String filteringMode,
            boolean tagFiltered,
            boolean locked,
            List<String> filterItems,
            List<Integer> lockedSlots
    ) {
        state.sortingMode = sortingMode;
        state.filteringMode = filteringMode;
        state.tagFiltered = tagFiltered;
        state.locked = locked;
        state.filterItems = filterItems;
        state.lockedSlots = lockedSlots;
    }

    /**
     * Assigns all values to a {@link ShulkerBlockEntityHolder}.
     */
    static void writeShulkerBlockEntityHolder(
            ShulkerBlockEntityHolder holder,
            ShulkerBoxBlockEntity shulker,
            String sortingMode,
            String filteringMode,
            boolean tagFiltered,
            boolean locked,
            List<String> filterItems,
            List<Integer> lockedSlots
    ) {
        holder.setSortingMode(sortingMode);
        holder.setFilteringMode(filteringMode);
        holder.setTagFiltered(tagFiltered);
        holder.setLocked(locked);
        holder.setFilterItems(filterItems);
        holder.setLockedSlots(lockedSlots);

        shulker.setChanged();
    }

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
     * @return if the shulker box is locked from sorting.
     */
    boolean isLocked();

    /**
     * Locks a shulker box from sorting.
     */
    void setLocked(boolean locked);

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