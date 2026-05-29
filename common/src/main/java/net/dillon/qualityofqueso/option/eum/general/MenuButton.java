package net.dillon.qualityofqueso.option.eum.general;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum MenuButton implements StringRepresentable {
    EVERYWHERE("Everywhere"),
    BOTTOM_LEFT("Bottom Left"),
    BOTTOM_RIGHT("Bottom Right"),
    TITLE_ONLY("Title Only"),
    OFF("OFF");

    public static final Codec<MenuButton> CODEC = StringRepresentable.fromEnum(MenuButton::values);
    private final String name;

    MenuButton(final String name) {
        this.name = name;
    }

    public boolean enabled() {
        return this != OFF;
    }

    public boolean left() {
        return this == BOTTOM_LEFT;
    }

    public boolean right() {
        return this == BOTTOM_RIGHT;
    }

    public boolean everywhere() {
        return this != OFF && this != TITLE_ONLY;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}