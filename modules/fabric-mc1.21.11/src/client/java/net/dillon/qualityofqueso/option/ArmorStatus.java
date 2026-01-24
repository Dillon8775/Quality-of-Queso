package net.dillon.qualityofqueso.option;

import com.mojang.serialization.Codec;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public enum ArmorStatus implements StringIdentifiable {
    ALWAYS(0, "always", "qualityofqueso.options.armor_status.always"),
    ON_CHANGE(1, "on_change", "qualityofqueso.options.armor_status.on_change"),
    OFF(2, "off", "qualityofqueso.options.armor_status.off");

    public static final Codec<ArmorStatus> Codec = StringIdentifiable.createCodec(ArmorStatus::values);
    private final int ordinal;
    private final String name;
    private final Text translationKey;

    ArmorStatus(final int ordinal, final String name, final String translationKey) {
        this.ordinal = ordinal;
        this.name = name;
        this.translationKey = Text.translatable(translationKey);
    }

    public boolean enabled() {
        return this == ALWAYS || this == ON_CHANGE;
    }

    public int getId() {
        return this.ordinal;
    }

    public Text getText() {
        return this.translationKey;
    }

    public String asString() {
        return this.name;
    }
}