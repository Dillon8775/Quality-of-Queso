package net.dillon.qualityofqueso.option.eum.searching;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum SearchBarColor implements StringRepresentable {
    DEFAULT("Default"),
    BLACK("§8Black");

    public static final Codec<SearchBarColor> CODEC = StringRepresentable.fromEnum(SearchBarColor::values);
    private final String name;

    SearchBarColor(final String name) {
        this.name = name;
    }

    public boolean black() {
        return this == BLACK;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}