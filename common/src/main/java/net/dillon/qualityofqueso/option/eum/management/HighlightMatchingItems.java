package net.dillon.qualityofqueso.option.eum.management;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.minecraft.util.StringRepresentable;

public enum HighlightMatchingItems implements StringRepresentable {
    ALWAYS(ModConstants.ALWAYS_SERIALIZED_NAME),
    ON_CTRL("§bOn Ctrl"),
    OFF(ModConstants.OFF_SERIALIZED_NAME);

    public static final Codec<HighlightMatchingItems> CODEC = StringRepresentable.fromEnum(HighlightMatchingItems::values);
    private final String name;

    HighlightMatchingItems(final String name) {
        this.name = name;
    }

    public boolean always() {
        return this == ALWAYS;
    }

    public boolean onCtrl() {
        return this == ON_CTRL;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}