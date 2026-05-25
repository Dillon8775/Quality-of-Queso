package net.dillon.qualityofqueso.option.eum.searching;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum SearchBarPosition implements StringRepresentable {
    OVERLAY("§6Overlay"),
    TOP("§bTop");

    public static final Codec<SearchBarPosition> CODEC = StringRepresentable.fromEnum(SearchBarPosition::values);
    private final String name;

    SearchBarPosition(final String name) {
        this.name = name;
    }

    public boolean top() {
        return this == TOP;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}