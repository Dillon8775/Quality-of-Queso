package net.dillon.qualityofqueso.option;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum Bows implements StringRepresentable {
    ON(0, "on", "qualityofqueso.options.fov_effects.bows.on"),
    QUICK(1, "quick", "qualityofqueso.options.fov_effects.bows.quick"),
    OFF(2, "off", "qualityofqueso.options.fov_effects.bows.off");

    public static final Codec<Bows> Codec = StringRepresentable.fromEnum(Bows::values);
    private final int ordinal;
    private final String name;
    private final Component translationKey;

    Bows(final int ordinal, final String name, final String translationKey) {
        this.ordinal = ordinal;
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean enabled() {
        return this == ON || this == QUICK;
    }

    public boolean on() {
        return this == ON;
    }

    public boolean isQuick() {
        return this == QUICK;
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