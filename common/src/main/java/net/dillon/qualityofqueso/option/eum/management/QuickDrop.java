package net.dillon.qualityofqueso.option.eum.management;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.util.StringRepresentable;

public enum QuickDrop implements StringRepresentable {
    BUTTON_OR_KEY(ModConstants.BUTTON_OR_KEY_SERIALIZED_NAME),
    KEY_ONLY(ModConstants.KEY_ONLY_SERIALIZED_NAME),
    OFF(ModConstants.OFF_SERIALIZED_NAME);

    public static final Codec<QuickDrop> CODEC = StringRepresentable.fromEnum(QuickDrop::values);
    private final String name;

    QuickDrop(final String name) {
        this.name = name;
    }

    public boolean buttonOrKey() {
        return this == BUTTON_OR_KEY;
    }

    public boolean buttonOrKeyOrKeyOnly() {
        return this == BUTTON_OR_KEY || this == KEY_ONLY;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}