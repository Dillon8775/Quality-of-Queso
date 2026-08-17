package net.dillon.qualityofqueso.option.eum.fov_effects;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.minecraft.util.StringRepresentable;

public enum Bows implements StringRepresentable {
    ENABLED(ModConstants.ENABLED_SERIALiZED_NAME),
    QUICK_PULL("§bQuick-Pull"),
    OFF(ModConstants.OFF_SERIALIZED_NAME);

    public static final Codec<Bows> CODEC = StringRepresentable.fromEnum(Bows::values);
    private final String name;

    Bows(final String name) {
        this.name = name;
    }

    public boolean enabled() {
        return this == ENABLED || this == QUICK_PULL;
    }

    public boolean quickPull() {
        return this == QUICK_PULL;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}