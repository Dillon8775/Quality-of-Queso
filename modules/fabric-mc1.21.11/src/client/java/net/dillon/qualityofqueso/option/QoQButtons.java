package net.dillon.qualityofqueso.option;

import com.mojang.serialization.Codec;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public enum QoQButtons implements StringIdentifiable {
    EVERYWHERE(0, "everywhere", "qualityofqueso.options.qoq_buttons.everywhere"),
    TITLE_ONLY(1, "title_only", "qualityofqueso.options.qoq_buttons.title_only"),
    OFF(2, "off", "qualityofqueso.options.qoq_buttons.off");

    public static final Codec<QoQButtons> Codec = StringIdentifiable.createCodec(QoQButtons::values);
    private final int ordinal;
    private final String name;
    private final Text translationKey;

    QoQButtons(final int ordinal, final String name, final String translationKey) {
        this.ordinal = ordinal;
        this.name = name;
        this.translationKey = Text.translatable(translationKey);
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

    public Text getText() {
        return this.translationKey;
    }

    public String asString() {
        return this.name;
    }
}