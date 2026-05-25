package net.dillon.qualityofqueso.option.eum.accessibility;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import static net.dillon.qualityofqueso.helper.ModHelper.clientOptionsInstance;

public enum WidgetTheme implements StringRepresentable {
    VANILLA("Vanilla"),
    DARK("Dark"),
    TRANSPARENT("Transparent");

    public static final Codec<WidgetTheme> CODEC = StringRepresentable.fromEnum(WidgetTheme::values);
    private final String name;

    WidgetTheme(final String name) {
        this.name = name;
    }

    public boolean searchBarTransparent() {
        return clientOptionsInstance().getSearchingOptions().searchBarColor.transparent() || this == TRANSPARENT;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}