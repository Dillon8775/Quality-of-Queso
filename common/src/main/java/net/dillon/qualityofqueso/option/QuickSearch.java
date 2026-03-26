package net.dillon.qualityofqueso.option;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum QuickSearch implements StringRepresentable {
    ON(0, "on", "qualityofqueso.options.quick_search.on"),
    RECIPE_BOOK(1, "recipe_book", "qualityofqueso.options.quick_search.recipe_book"),
    SEARCH_BAR(2, "search_bar", "qualityofqueso.options.quick_search.search_bar"),
    OFF(3, "off", "qualityofqueso.options.quick_search.off");

    public static final Codec<QuickSearch> Codec = StringRepresentable.fromEnum(QuickSearch::values);
    private final int ordinal;
    private final String name;
    private final Component translationKey;

    QuickSearch(final int ordinal, final String name, final String translationKey) {
        this.ordinal = ordinal;
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