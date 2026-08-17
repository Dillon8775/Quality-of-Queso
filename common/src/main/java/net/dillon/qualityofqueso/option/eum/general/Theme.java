package net.dillon.qualityofqueso.option.eum.general;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import static net.dillon.qualityofqueso.option.OptionInstances.client;

public enum Theme implements StringRepresentable {
    VANILLA("Vanilla"),
    DARK("Dark"),
    TRUE_DARK("§7True Dark"),
    TRANSPARENT("Transparent");

    public static final Codec<Theme> CODEC = StringRepresentable.fromEnum(Theme::values);
    private final String name;

    Theme(final String name) {
        this.name = name;
    }

    public boolean searchBarTransparent() {
        return client().searching().searchBarColor.transparent() || this == TRANSPARENT;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}