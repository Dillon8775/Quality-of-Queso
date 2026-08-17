package net.dillon.qualityofqueso.option.eum.fov_effects;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.minecraft.util.StringRepresentable;

public enum PotionEffects implements StringRepresentable {
    ENABLED(ModConstants.ENABLED_SERIALiZED_NAME),
    NON_BEACON("§bNon-Beacon"),
    OFF(ModConstants.OFF_SERIALIZED_NAME);

    public static final Codec<PotionEffects> CODEC = StringRepresentable.fromEnum(PotionEffects::values);
    private final String name;

    PotionEffects(final String name) {
        this.name = name;
    }

    public boolean enabled() {
        return this != OFF;
    }

    public boolean nonBeacon() {
        return this == NON_BEACON;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}