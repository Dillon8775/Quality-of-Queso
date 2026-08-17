package net.dillon.qualityofqueso.option.eum.hud;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.minecraft.util.StringRepresentable;

public enum ArmorStatus implements StringRepresentable {
    ALWAYS(ModConstants.ALWAYS_SERIALIZED_NAME),
    ON_UPDATE("§bOn Update"),
    OFF(ModConstants.OFF_SERIALIZED_NAME);

    public static final Codec<ArmorStatus> CODEC = StringRepresentable.fromEnum(ArmorStatus::values);
    private final String name;

    ArmorStatus(final String name) {
        this.name = name;
    }

    public boolean off() {
        return this == OFF;
    }

    public boolean onUpdate() {
        return this == ON_UPDATE;
    }

    public boolean always() {
        return this == ALWAYS;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}