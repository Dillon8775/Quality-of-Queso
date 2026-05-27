package net.dillon.qualityofqueso.option.eum.management;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.util.StringRepresentable;

public enum FilteringButton implements StringRepresentable {
    ALWAYS(ModConstants.ALWAYS_SERIALIZED_NAME),
    FILTERED_CONTAINERS_ONLY("§6Filtered Containers §6§oOnly");

    public static final Codec<FilteringButton> CODEC = StringRepresentable.fromEnum(FilteringButton::values);
    private final String name;

    FilteringButton(final String name) {
        this.name = name;
    }

    public boolean filteredContainersOnly() {
        return this == FILTERED_CONTAINERS_ONLY;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}