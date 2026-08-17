package net.dillon.qualityofqueso.option.eum.management;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.minecraft.util.StringRepresentable;

public enum IncludeHotbar implements StringRepresentable {
    ALWAYS(ModConstants.ALWAYS_SERIALIZED_NAME),
    CONTAINER_SCREENS_ONLY("§6Container Screens Only"),
    OFF(ModConstants.OFF_SERIALIZED_NAME);

    public static final Codec<IncludeHotbar> CODEC = StringRepresentable.fromEnum(IncludeHotbar::values);
    private final String name;

    IncludeHotbar(final String name) {
        this.name = name;
    }

    public boolean containerScreensOnly() {
        return this == CONTAINER_SCREENS_ONLY;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}