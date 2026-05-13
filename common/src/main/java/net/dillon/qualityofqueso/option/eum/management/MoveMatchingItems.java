package net.dillon.qualityofqueso.option.eum.management;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.option.eum.OptionEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum MoveMatchingItems implements StringRepresentable, OptionEnum {
    ALWAYS("always", "qualityofqueso.options.move_matching_items.always"),
    FILTERED_CONTAINERS_ONLY("filtered_containers_only", "qualityofqueso.options.move_matching_items.filtered_containers_only");

    public static final Codec<MoveMatchingItems> CODEC = StringRepresentable.fromEnum(MoveMatchingItems::values);
    private final String name;
    private final Component translationKey;

    MoveMatchingItems(final String name, final String translationKey) {
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean filteredContainersOnly() {
        return this == FILTERED_CONTAINERS_ONLY;
    }

    public String getSerializedName() {
        return this.name;
    }

    @Override
    public Component getTranslationKey() {
        return this.translationKey;
    }
}