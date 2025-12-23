package net.dillon.qualityofqueso.option;

import com.mojang.serialization.Codec;
import net.minecraft.util.OptionEnum;
import net.minecraft.util.StringRepresentable;

/**
 * Defines where the Quality of Queso configuration button can appear.
 */
public enum QoQButtons implements OptionEnum, StringRepresentable {
    EVERYWHERE(0, "everywhere", "qualityofqueso.options.qoq_buttons.everywhere"),
    TITLE_ONLY(1, "title_only", "qualityofqueso.options.qoq_buttons.title_only"),
    OFF(2, "off", "qualityofqueso.options.qoq_buttons.off");

    public static final Codec<QoQButtons> Codec = StringRepresentable.fromEnum(QoQButtons::values);
    private final int ordinal;
    private final String name;
    private final String translationKey;

    QoQButtons(final int ordinal, final String name, final String translationKey) {
        this.ordinal = ordinal;
        this.name = name;
        this.translationKey = translationKey;
    }

    public boolean everywhere() {
        return this == EVERYWHERE;
    }

    public boolean titleOnly() {
        return this == TITLE_ONLY;
    }

    public int getId() {
        return this.ordinal;
    }

    public String getSerializedName() {
        return this.name;
    }

    public String getKey() {
        return this.translationKey;
    }
}