package net.dillon.qualityofqueso.option.eum.management;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum ButtonLayout implements StringRepresentable {
    HORIZONTAL(0, "on", "qualityofqueso.options.button_layout.horizontal"),
    VERTICAL(1, "quick", "qualityofqueso.options.button_layout.vertical");

    public static final Codec<ButtonLayout> Codec = StringRepresentable.fromEnum(ButtonLayout::values);
    private final int ordinal;
    private final String name;
    private final Component translationKey;

    ButtonLayout(final int ordinal, final String name, final String translationKey) {
        this.ordinal = ordinal;
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean horizontal() {
        return this == HORIZONTAL;
    }

    public boolean vertical() {
        return this == VERTICAL;
    }

    public int getId() {
        return this.ordinal;
    }

    public String getSerializedName() {
        return this.name;
    }

    public Component getText() {
        return this.translationKey;
    }
}