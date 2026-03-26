package net.dillon.qualityofqueso.option;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum DisplayIncludeHotbar implements StringRepresentable {
    ALWAYS(0, "always", "qualityofqueso.options.display_include_hotbar.always"),
    CONTAINER_SCREENS_ONLY(1, "container_screens_only", "qualityofqueso.options.display_include_hotbar.container_screens_only"),;

    public static final Codec<DisplayIncludeHotbar> Codec = StringRepresentable.fromEnum(DisplayIncludeHotbar::values);
    private final int ordinal;
    private final String name;
    private final Component translationKey;

    DisplayIncludeHotbar(final int ordinal, final String name, final String translationKey) {
        this.ordinal = ordinal;
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean containerScreensOnly() {
        return this == CONTAINER_SCREENS_ONLY;
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