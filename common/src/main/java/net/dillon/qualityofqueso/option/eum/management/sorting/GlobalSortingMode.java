package net.dillon.qualityofqueso.option.eum.management.sorting;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.util.StringRepresentable;

public enum GlobalSortingMode implements StringRepresentable {
    ALPHABETICALLY(ModConstants.ALPHABETICALLY_SERIALIZED_NAME),
    BY_TAG(ModConstants.BY_TAG_SERIALIZED_NAME),
    DESCENDING(ModConstants.DESCENDING_SERIALIZED_NAME),
    ASCENDING(ModConstants.ASCENDING_SERIALIZED_NAME),
    CREATIVE_MENU(ModConstants.CREATIVE_MENU_SERIALIZED_NAME);

    public static final Codec<GlobalSortingMode> CODEC = StringRepresentable.fromEnum(GlobalSortingMode::values);
    private final String name;

    GlobalSortingMode(final String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}