package net.dillon.qualityofqueso.option.eum.searching;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.option.eum.OptionEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum SearchBarPosition implements StringRepresentable, OptionEnum {
    OVERLAY("overlay", "qualityofqueso.options.search_bar_position.overlay"),
    TOP("top", "qualityofqueso.options.search_bar_position.top");

    public static final Codec<SearchBarPosition> CODEC = StringRepresentable.fromEnum(SearchBarPosition::values);
    private final String name;
    private final Component translationKey;

    SearchBarPosition(final String name, final String translationKey) {
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean top() {
        return this == TOP;
    }

    @Override
    public @NonNull String getSerializedName() {
        return this.name;
    }

    @Override
    public Component getTranslationKey() {
        return this.translationKey;
    }
}