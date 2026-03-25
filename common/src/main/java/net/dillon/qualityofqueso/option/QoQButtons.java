package net.dillon.qualityofqueso.option;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

/**
 * Defines where the Quality of Queso configuration button can appear.
 */
public enum QoQButtons implements StringRepresentable {
    EVERYWHERE(0, "everywhere", "qualityofqueso.options.qoq_buttons.everywhere"),
    BOTTOM_LEFT(1, "bottom", "qualityofqueso.options.qoq_buttons.bottom_left"),
    BOTTOM_RIGHT(1, "bottom", "qualityofqueso.options.qoq_buttons.bottom_right"),
    TITLE_ONLY(2, "title_only", "qualityofqueso.options.qoq_buttons.title_only"),
    OFF(3, "off", "qualityofqueso.options.qoq_buttons.off");

    public static final Codec<QoQButtons> Codec = StringRepresentable.fromEnum(QoQButtons::values);
    private final int ordinal;
    private final String name;
    private final Component translationKey;

    QoQButtons(final int ordinal, final String name, final String translationKey) {
        this.ordinal = ordinal;
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean left() {
        return this == BOTTOM_LEFT;
    }

    public boolean right() {
        return this == BOTTOM_RIGHT;
    }

    public boolean everywhere() {
        return this != OFF && this != TITLE_ONLY;
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