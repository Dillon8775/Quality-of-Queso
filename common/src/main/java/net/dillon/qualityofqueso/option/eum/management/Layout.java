package net.dillon.qualityofqueso.option.eum.management;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum Layout implements StringRepresentable {
    HORIZONTAL("§aHorizontal"),
    VERTICAL("§6Vertical");

    public static final Codec<Layout> CODEC = StringRepresentable.fromEnum(Layout::values);
    private final String name;

    Layout(final String name) {
        this.name = name;
    }

    public boolean horizontal() {
        return this == HORIZONTAL;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}