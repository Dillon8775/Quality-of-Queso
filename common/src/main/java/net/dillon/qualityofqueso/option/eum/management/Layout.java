package net.dillon.qualityofqueso.option.eum.management;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.option.eum.OptionEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum Layout implements StringRepresentable, OptionEnum {
    HORIZONTAL("horizontal", "qualityofqueso.options.layout.horizontal"),
    VERTICAL("vertical", "qualityofqueso.options.layout.vertical");

    public static final Codec<Layout> CODEC = StringRepresentable.fromEnum(Layout::values);
    private final String name;
    private final Component translationKey;

    Layout(final String name, final String translationKey) {
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean horizontal() {
        return this == HORIZONTAL;
    }

    public @NonNull String getSerializedName() {
        return this.name;
    }

    @Override
    public Component getTranslationKey() {
        return this.translationKey;
    }
}