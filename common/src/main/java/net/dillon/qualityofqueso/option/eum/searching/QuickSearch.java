package net.dillon.qualityofqueso.option.eum.searching;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.util.StringRepresentable;

public enum QuickSearch implements StringRepresentable {
    ENABLED(ModConstants.ENABLED_SERIALiZED_NAME),
    RECIPE_BOOK("§bRecipe Book"),
    SEARCH_BAR("§6Search Bar"),
    OFF(ModConstants.OFF_SERIALIZED_NAME);

    public static final Codec<QuickSearch> CODEC = StringRepresentable.fromEnum(QuickSearch::values);
    private final String name;

    QuickSearch(final String name) {
        this.name = name;
    }

    public boolean enabled() {
        return this != OFF;
    }

    public boolean on() {
        return this == ENABLED;
    }

    public boolean recipeBook() {
        return this == RECIPE_BOOK;
    }

    public boolean searchBar() {
        return this == SEARCH_BAR;
    }

    public boolean creativeMenu() {
        return this != OFF && this != RECIPE_BOOK;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}