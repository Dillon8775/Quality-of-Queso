package net.dillon.qualityofqueso.option;

import com.mojang.serialization.Codec;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public enum QuickDrop implements StringIdentifiable {
    ON(0, "on", "qualityofqueso.gui.on"),
    SHORTCUT_KEY(1, "shortcut_key", "qualityofqueso.options.quick_drop.shortcut_key"),
    OFF(2, "off", "qualityofqueso.gui.off");

    public static final Codec<QuickDrop> Codec = StringIdentifiable.createCodec(QuickDrop::values);
    private final int ordinal;
    private final String name;
    private final Text translationKey;

    QuickDrop(final int ordinal, final String name, final String translationKey) {
        this.ordinal = ordinal;
        this.name = name;
        this.translationKey = Text.translatable(translationKey);
    }

    public boolean all() {
        return this == ON;
    }

    public boolean either() {
        return this.all() || this == SHORTCUT_KEY;
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