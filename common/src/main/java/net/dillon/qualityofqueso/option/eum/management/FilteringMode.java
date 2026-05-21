package net.dillon.qualityofqueso.option.eum.management;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.option.eum.OptionEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum FilteringMode implements StringRepresentable, OptionEnum {
    NONE("none", "qualityofqueso.options.filtering_mode.none"),
    MATCHING("matching", "qualityofqueso.options.filtering_mode.matching"),
    CURRENT_STACKS("current_stacks", "qualityofqueso.options.filtering_mode.current_stacks");

    public static final Codec<FilteringMode> CODEC = StringRepresentable.fromEnum(FilteringMode::values);
    private final String name;
    private final Component translationKey;

    FilteringMode(final String name, final String translationKey) {
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean matching() {
        return this != MATCHING;
    }

    public boolean matchingOrCurrentStacks() {
        return this != NONE;
    }

    public String getSerializedName() {
        return this.name;
    }

    @Override
    public Component getTranslationKey() {
        return this.translationKey;
    }
}