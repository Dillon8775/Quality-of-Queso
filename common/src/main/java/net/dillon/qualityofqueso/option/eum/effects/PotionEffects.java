package net.dillon.qualityofqueso.option.eum.effects;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum PotionEffects implements StringRepresentable {
    ON(0, "on", "qualityofqueso.options.fov_effects.potion_effects.on"),
    NON_BEACON(1, "non_beacon", "qualityofqueso.options.fov_effects.potion_effects.non_beacon"),
    OFF(2, "off", "qualityofqueso.options.fov_effects.potion_effects.off");

    public static final Codec<PotionEffects> Codec = StringRepresentable.fromEnum(PotionEffects::values);
    private final int ordinal;
    private final String name;
    private final Component translationKey;

    PotionEffects(final int ordinal, final String name, final String translationKey) {
        this.ordinal = ordinal;
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean enabled() {
        return this != OFF;
    }

    public boolean nonBeacon() {
        return this == NON_BEACON;
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