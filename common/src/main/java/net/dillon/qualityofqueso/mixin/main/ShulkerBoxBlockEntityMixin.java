package net.dillon.qualityofqueso.mixin.main;

import net.dillon.dillonlib.annotation.Dill;
import net.dillon.dillonlib.annotation.DillType;
import net.dillon.qualityofqueso.util.ShulkerStateHolder;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.dillon.qualityofqueso.util.ModConstants.*;

/**
 * Persists shulker filtering state directly on each shulker block entity.
 */
@Dill(DillType.COMMON)
@Mixin(ShulkerBoxBlockEntity.class)
public class ShulkerBoxBlockEntityMixin implements ShulkerStateHolder {
    @Unique
    private boolean filtered = false;
    @Unique
    private boolean tagFiltered = false;
    @Unique
    private List<String> filterItems = new ArrayList<>();
    @Unique
    private List<Integer> lockedSlots = new ArrayList<>();
    @Unique
    private String sortingMode = "";

    /**
     * @return whether this shulker is filtered.
     */
    @Override
    public boolean isFiltered() {
        return this.filtered;
    }

    /**
     * Sets whether this shulker is filtered.
     */
    @Override
    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
    }

    /**
     * @return whether this shulker uses tag filtering.
     */
    @Override
    public boolean isTagFiltered() {
        return this.tagFiltered;
    }

    /**
     * Sets whether this shulker uses tag filtering.
     */
    @Override
    public void setTagFiltered(boolean tagFiltered) {
        this.tagFiltered = tagFiltered;
    }

    /**
     * @return the filter placeholder item identifiers.
     */
    @Override
    public List<String> getFilterItems() {
        return new ArrayList<>(this.filterItems);
    }

    /**
     * Replaces the filter placeholder item identifiers.
     */
    @Override
    public void setFilterItems(List<String> filterItems) {
        if (filterItems == null || filterItems.isEmpty()) {
            this.filterItems = new ArrayList<>();
            return;
        }
        this.filterItems = filterItems.stream()
                .filter(s -> s != null && !s.isBlank())
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * @return the locked slot indices.
     */
    @Override
    public List<Integer> getLockedSlots() {
        return new ArrayList<>(this.lockedSlots);
    }

    /**
     * Replaces locked slot indices.
     */
    @Override
    public void setLockedSlots(List<Integer> lockedSlots) {
        if (lockedSlots == null || lockedSlots.isEmpty()) {
            this.lockedSlots = new ArrayList<>();
            return;
        }
        this.lockedSlots = lockedSlots.stream()
                .distinct()
                .sorted()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Gets the sorting mode for a box.
     */
    @Override
    public String getSortingMode() {
        return this.sortingMode;
    }

    /**
     * Sets the sorting mode for a box.
     */
    @Override
    public void setSortingMode(String sortingMode) {
        if (sortingMode == null || sortingMode.isBlank()) {
            this.sortingMode = "";
            return;
        }
        this.sortingMode = sortingMode;
    }

    /**
     * Loads shulker state from block-entity storage.
     */
    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void loadShulkerIdentity(ValueInput input, CallbackInfo ci) {
        this.filtered = input.getBooleanOr(FILTERED, false);
        this.tagFiltered = input.getBooleanOr(TAG_FILTERED, false);

        String filterItemsRaw = input.getStringOr(FILTER_ITEMS, "");
        if (filterItemsRaw.isBlank()) {
            this.filterItems = new ArrayList<>();
        } else {
            this.filterItems = Arrays.stream(filterItemsRaw.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .distinct()
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        int[] locked = input.getIntArray(LOCKED_SLOTS).orElse(new int[0]);
        this.lockedSlots = Arrays.stream(locked)
                .boxed()
                .distinct()
                .sorted()
                .collect(Collectors.toCollection(ArrayList::new));
        this.sortingMode = input.getStringOr(SORTING_MODE, "");
    }

    /**
     * Saves shulker state to block-entity storage.
     */
    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void saveShulkerIdentity(ValueOutput output, CallbackInfo ci) {
        output.putBoolean(FILTERED, this.filtered);
        output.putBoolean(TAG_FILTERED, this.tagFiltered);
        output.putString(FILTER_ITEMS, String.join(",", this.filterItems));
        output.putIntArray(LOCKED_SLOTS, this.lockedSlots.stream().mapToInt(Integer::intValue).toArray());
        output.putString(SORTING_MODE, this.sortingMode);
    }
}