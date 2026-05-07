package net.dillon.qualityofqueso.option.eum.accessibility;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.option.eum.OptionEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

import static net.dillon.qualityofqueso.helper.ModHelper.options;

public enum WidgetTheme implements StringRepresentable, OptionEnum {
    VANILLA("vanilla", "qualityofqueso.options.widget_theme.vanilla"),
    DARK("dark", "qualityofqueso.options.widget_theme.dark"),
    TRANSPARENT("transparent", "qualityofqueso.options.widget_theme.transparent");

    public static final Codec<WidgetTheme> CODEC = StringRepresentable.fromEnum(WidgetTheme::values);
    private final String name;
    private final Component translationKey;

    WidgetTheme(final String name, final String translationKey) {
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean searchBarTransparent() {
        return options().searching.searchBarColor.transparent() || this == TRANSPARENT;
    }

    public @NonNull String getSerializedName() {
        return this.name;
    }

    @Override
    public Component getTranslationKey() {
        return this.translationKey;
    }
}