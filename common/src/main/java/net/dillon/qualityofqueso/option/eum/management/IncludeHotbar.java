package net.dillon.qualityofqueso.option.eum.management;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.option.eum.OptionEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum IncludeHotbar implements StringRepresentable, OptionEnum {
    ALWAYS("always", "qualityofqueso.options.include_hotbar.always"),
    CONTAINER_SCREENS_ONLY("container_screens_only", "qualityofqueso.options.include_hotbar.container_screens_only"),
    OFF("off", "qualityofqueso.options.include_hotbar.off");

    public static final Codec<IncludeHotbar> CODEC = StringRepresentable.fromEnum(IncludeHotbar::values);
    private final String name;
    private final Component translationKey;

    IncludeHotbar(final String name, final String translationKey) {
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean containerScreensOnly() {
        return this == CONTAINER_SCREENS_ONLY;
    }

    public @NonNull String getSerializedName() {
        return this.name;
    }

    @Override
    public Component getTranslationKey() {
        return this.translationKey;
    }
}