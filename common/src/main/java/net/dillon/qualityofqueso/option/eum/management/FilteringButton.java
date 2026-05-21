package net.dillon.qualityofqueso.option.eum.management;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.option.eum.OptionEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum FilteringButton implements StringRepresentable, OptionEnum {
    ALWAYS("always", "qualityofqueso.options.filtering.always"),
    FILTERED_CONTAINERS_ONLY("filtered_containers_only", "qualityofqueso.options.filtering.filtered_containers_only");

    public static final Codec<FilteringButton> CODEC = StringRepresentable.fromEnum(FilteringButton::values);
    private final String name;
    private final Component translationKey;

    FilteringButton(final String name, final String translationKey) {
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean filteredContainersOnly() {
        return this == FILTERED_CONTAINERS_ONLY;
    }

    public @NonNull String getSerializedName() {
        return this.name;
    }

    @Override
    public Component getTranslationKey() {
        return this.translationKey;
    }
}