package net.dillon.qualityofqueso.option.eum.searching;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.option.eum.OptionEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum SearchBarColor implements StringRepresentable, OptionEnum {
    BLACK("black", "qualityofqueso.options.search_bar_color.black");

    public static final Codec<SearchBarColor> CODEC = StringRepresentable.fromEnum(SearchBarColor::values);
    private final String name;
    private final Component translationKey;

    SearchBarColor(final String name, final String translationKey) {
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean black() {
        return this == BLACK;
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