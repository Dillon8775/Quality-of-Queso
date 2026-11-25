package net.dillon.qualityofqueso.option.eum.management.sorting;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.minecraft.util.StringRepresentable;

public enum Sorting implements StringRepresentable {
    BUTTON_OR_SHORTCUT_KEY(ModConstants.BUTTON_OR_SHORTCUT_KEY_SERIALIZED_NAME),
    SHORTCUT_KEY_ONLY(ModConstants.SHORTCUT_KEY_ONLY_SERIALIZED_NAME),
    OFF(ModConstants.OFF_SERIALIZED_NAME);

    public static final Codec<Sorting> CODEC = StringRepresentable.fromEnum(Sorting::values);
    private final String name;

    Sorting(final String name) {
        this.name = name;
    }

    public boolean buttonOrShortcutKey() {
        return this == BUTTON_OR_SHORTCUT_KEY;
    }

    public boolean enabled() {
        return this == BUTTON_OR_SHORTCUT_KEY || this == SHORTCUT_KEY_ONLY;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}