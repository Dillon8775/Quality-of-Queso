package net.dillon.qualityofqueso.option.eum.management;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum ContainerSorting implements StringRepresentable {
    SHORTCUT_KEY_OR_BUTTON(0, "shortcut_key_or_button", "qualityofqueso.options.shortcut_key_or_button"),
    SHORTCUT_KEY_ONLY(1, "shortcut_key_only", "qualityofqueso.options.shortcut_key_only"),
    OFF(2, "off", "qualityofqueso.options.management_button.off");

    public static final Codec<ContainerSorting> Codec = StringRepresentable.fromEnum(ContainerSorting::values);
    private final int ordinal;
    private final String name;
    private final Component translationKey;

    ContainerSorting(final int ordinal, final String name, final String translationKey) {
        this.ordinal = ordinal;
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
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

    public String getSerializedName() {
        return this.name;
    }

    public Component getText() {
        return this.translationKey;
    }
}