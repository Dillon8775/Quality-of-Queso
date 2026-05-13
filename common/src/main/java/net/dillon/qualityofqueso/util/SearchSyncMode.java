package net.dillon.qualityofqueso.util;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.option.eum.OptionEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum SearchSyncMode implements StringRepresentable, OptionEnum {
    OFF("off", "qualityofqueso.options.sync.off"),
    PUSH("push", "qualityofqueso.options.sync.push"),
    PULL("pull", "qualityofqueso.options.sync.pull"),
    BOTH("both", "qualityofqueso.options.sync.both");

    public static final Codec<SearchSyncMode> CODEC = StringRepresentable.fromEnum(SearchSyncMode::values);
    private final String name;
    private final Component translationKey;

    SearchSyncMode(String name, String translationKey) {
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
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