package net.dillon.qualityofqueso.option.eum.effects;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum Bows implements StringRepresentable {
    ON(0, "on", "qualityofqueso.options.fov_effects.bows.on"),
    QUICK_PULL(1, "quick_pull", "qualityofqueso.options.fov_effects.bows.quick_pull"),
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
        return this == ON || this == QUICK_PULL;
    }

    public boolean on() {
        return this == ON;
    }

    public boolean quickPull() {
        return this == QUICK_PULL;
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