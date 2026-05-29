package net.dillon.qualityofqueso.option.eum.general;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum Tooltips implements StringRepresentable {
    DEFAULT("Default"),
    OVERLAY("Overlay"),
    OFF("OFF");

    public static final Codec<Tooltips> CODEC = StringRepresentable.fromEnum(Tooltips::values);
    private final String name;

    Tooltips(final String name) {
        this.name = name;
    }

    public boolean enabled() {
        return this != OFF;
    }

    public boolean ddefault() {
        return this == DEFAULT;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}