package net.dillon.qualityofqueso.option.eum.accessibility;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum Theme implements StringRepresentable {
    VANILLA("Vanilla"),
    DARK("Dark"),
    TRANSPARENT("Transparent");

    public static final Codec<Theme> CODEC = StringRepresentable.fromEnum(Theme::values);
    private final String name;

    Theme(final String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}