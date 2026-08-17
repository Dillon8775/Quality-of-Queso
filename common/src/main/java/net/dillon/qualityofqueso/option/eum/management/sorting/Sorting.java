package net.dillon.qualityofqueso.option.eum.management.sorting;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.minecraft.util.StringRepresentable;

public enum Sorting implements StringRepresentable {
    BUTTON_OR_KEY(ModConstants.BUTTON_OR_KEY_SERIALIZED_NAME),
    KEY_ONLY(ModConstants.KEY_ONLY_SERIALIZED_NAME),
    OFF(ModConstants.OFF_SERIALIZED_NAME);

    public static final Codec<Sorting> CODEC = StringRepresentable.fromEnum(Sorting::values);
    private final String name;

    Sorting(final String name) {
        this.name = name;
    }

    public boolean buttonOrKey() {
        return this == BUTTON_OR_KEY;
    }

    public boolean any() {
        return this == BUTTON_OR_KEY || this == KEY_ONLY;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}