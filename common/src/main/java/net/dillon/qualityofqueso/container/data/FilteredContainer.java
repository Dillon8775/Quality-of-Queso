package net.dillon.qualityofqueso.container.data;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Stores data for filtered containers.
 */
public class FilteredContainer {
    public String type = "";
    public String dimension = "";

    /**
     * Block positions belonging to this container.
     * A single chest has one position; a double chest has two.
     */
    public List<int[]> positions = new ArrayList<>();

    public FilteredContainer() {
    }

    public FilteredContainer(String type, String dimension, BlockPos pos) {
        this(type, dimension, List.of(pos));
    }

    public FilteredContainer(String type, String dimension, BlockPos first, BlockPos second) {
        this(type, dimension, List.of(first, second));
    }

    public FilteredContainer(String type, String dimension, List<BlockPos> positions) {
        this.type = type;
        this.dimension = dimension;
        this.positions = positions.stream()
                .map(position -> new int[]{
                        position.getX(),
                        position.getY(),
                        position.getZ()
                })
                .toList()
                .stream()
                .map(int[]::clone)
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }

    /**
     * @return whether both identities describe the same container.
     * Position order does not matter.
     */
    public boolean matches(FilteredContainer other) {
        if (other == null
                || !Objects.equals(type, other.type)
                || !Objects.equals(dimension, other.dimension)) {
            return false;
        }

        if (positions == null || other.positions == null || positions.size() != other.positions.size()) {
            return false;
        }

        List<int[]> unmatched = new ArrayList<>(positions);

        for (int[] otherPosition : other.positions) {
            int matchingIndex = -1;

            for (int i = 0; i < unmatched.size(); i++) {
                if (Arrays.equals(unmatched.get(i), otherPosition)) {
                    matchingIndex = i;
                    break;
                }
            }

            if (matchingIndex == -1) {
                return false;
            }

            unmatched.remove(matchingIndex);
        }

        return true;
    }
}