package net.dillon.qualityofqueso.option.eum.misc;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.util.StringRepresentable;

public enum ElytraAlarm implements StringRepresentable {
    ENABLED(ModConstants.ENABLED_SERIALiZED_NAME),
    INDICATOR_ONLY("§bIndicator Only"),
    OFF(ModConstants.OFF_SERIALIZED_NAME);

    public static final Codec<ElytraAlarm> CODEC = StringRepresentable.fromEnum(ElytraAlarm::values);
    private final String name;

    ElytraAlarm(final String name) {
        this.name = name;
    }

    public boolean enabled() {
        return this != OFF;
    }

    public boolean indicatorOnly() {
        return this == INDICATOR_ONLY;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}