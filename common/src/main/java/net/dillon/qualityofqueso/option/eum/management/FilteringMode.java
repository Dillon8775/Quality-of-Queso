package net.dillon.qualityofqueso.option.eum.management;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum FilteringMode implements StringRepresentable {
    NONE("none"),
    MATCHING("matching"),
    CURRENT_STACKS("current_stacks");

    public static final Codec<FilteringMode> CODEC = StringRepresentable.fromEnum(FilteringMode::values);
    private final String name;

    FilteringMode(final String name) {
        this.name = name;
    }

    public boolean matching() {
        return this != MATCHING;
    }

    public boolean matchingOrCurrentStacks() {
        return this != NONE;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}