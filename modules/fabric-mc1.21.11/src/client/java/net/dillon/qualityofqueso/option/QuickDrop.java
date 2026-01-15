package net.dillon.qualityofqueso.option;

import com.mojang.serialization.Codec;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public enum QuickDrop implements StringIdentifiable {
    SHORTCUT_KEY_OR_BUTTON(0, "shortcut_key_or_button", "qualityofqueso.options.shortcut_key_or_button"),
    SHORTCUT_KEY_ONLY(1, "shortcut_key_only", "qualityofqueso.options.shortcut_key_only"),
    OFF(2, "off", "qualityofqueso.options.management_button.off");

    public static final Codec<QuickDrop> Codec = StringIdentifiable.createCodec(QuickDrop::values);
    private final int ordinal;
    private final String name;
    private final Text translationKey;

    QuickDrop(final int ordinal, final String name, final String translationKey) {
        this.ordinal = ordinal;
        this.name = name;
        this.translationKey = Text.translatable(translationKey);
    }

    public boolean shortcutOrButton() {
        return this == SHORTCUT_KEY_OR_BUTTON;
    }

    public boolean orKeyOnly() {
        return this == SHORTCUT_KEY_OR_BUTTON || this == SHORTCUT_KEY_ONLY;
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