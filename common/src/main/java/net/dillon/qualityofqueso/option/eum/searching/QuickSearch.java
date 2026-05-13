package net.dillon.qualityofqueso.option.eum.searching;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.option.eum.OptionEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum QuickSearch implements StringRepresentable, OptionEnum {
    ON("on", "qualityofqueso.options.quick_search.on"),
    RECIPE_BOOK("recipe_book", "qualityofqueso.options.quick_search.recipe_book"),
    SEARCH_BAR("search_bar", "qualityofqueso.options.quick_search.search_bar"),
    OFF("off", "qualityofqueso.options.quick_search.off");

    public static final Codec<QuickSearch> CODEC = StringRepresentable.fromEnum(QuickSearch::values);
    private final String name;
    private final Component translationKey;

    QuickSearch(final String name, final String translationKey) {
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean enabled() {
        return this != OFF;
    }

    public boolean on() {
        return this == ON;
    }

    public boolean recipeBook() {
        return this == RECIPE_BOOK;
    }

    public boolean searchBar() {
        return this == SEARCH_BAR;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    @Override
    public Component getTranslationKey() {
        return this.translationKey;
    }
}