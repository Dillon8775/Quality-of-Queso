package net.dillon.qualityofqueso.option;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum ArmorStatus implements StringRepresentable {
    ALWAYS(0, "always", "qualityofqueso.options.armor_status.always"),
    ON_CHANGE(1, "on_change", "qualityofqueso.options.armor_status.on_change"),
    OFF(2, "off", "qualityofqueso.options.armor_status.off");

    public static final Codec<ArmorStatus> Codec = StringRepresentable.fromEnum(ArmorStatus::values);
    private final int ordinal;
    private final String name;
    private final Component translationKey;

    ArmorStatus(final int ordinal, final String name, final String translationKey) {
        this.ordinal = ordinal;
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean enabled() {
        return this == ALWAYS || this == ON_CHANGE;
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