package net.dillon.qualityofqueso.option.eum.management.sorting;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.option.eum.OptionEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum GlobalSortingMode implements StringRepresentable, OptionEnum {
    ALPHABETICALLY("alphabetically", "qualityofqueso.options.global_sorting_mode.alphabetically"),
    BY_TAG("by_tag", "qualityofqueso.options.global_sorting_mode.by_tag"),
    DESCENDING("descending", "qualityofqueso.options.global_sorting_mode.descending"),
    ASCENDING("ascending", "qualityofqueso.options.global_sorting_mode.ascending"),
    CREATIVE_MENU("creative_menu", "qualityofqueso.options.global_sorting_mode.creative_menu");

    public static final Codec<GlobalSortingMode> CODEC = StringRepresentable.fromEnum(GlobalSortingMode::values);
    private final String name;
    private final Component translationKey;

    GlobalSortingMode(final String name, final String translationKey) {
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public @NonNull String getSerializedName() {
        return this.name;
    }

    @Override
    public Component getTranslationKey() {
        return this.translationKey;
    }
}