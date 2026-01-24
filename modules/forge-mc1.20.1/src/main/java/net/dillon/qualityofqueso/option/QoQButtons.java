package net.dillon.qualityofqueso.option;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

/**
 * Defines where the Quality of Queso configuration button can appear.
 */
public enum QoQButtons implements StringRepresentable {
    EVERYWHERE(0, "everywhere", "qualityofqueso.options.qoq_buttons.everywhere"),
    TITLE_ONLY(1, "title_only", "qualityofqueso.options.qoq_buttons.title_only"),
    OFF(2, "off", "qualityofqueso.options.qoq_buttons.off");

    public static final Codec<QoQButtons> Codec = StringRepresentable.fromEnum(QoQButtons::values);
    private final int ordinal;
    private final String name;
    private final Component translationKey;

    QoQButtons(final int ordinal, final String name, final String translationKey) {
        this.ordinal = ordinal;
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
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

    public String getKey() {
        return this.name;
    }

    public Component getCaption() {
        return this.translationKey;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}