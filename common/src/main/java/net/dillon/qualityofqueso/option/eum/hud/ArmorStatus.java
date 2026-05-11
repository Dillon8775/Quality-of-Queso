package net.dillon.qualityofqueso.option.eum.hud;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.option.eum.OptionEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum ArmorStatus implements StringRepresentable, OptionEnum {
    ALWAYS("always", "qualityofqueso.options.armor_status.always"),
    ON_UPDATE("on_update", "qualityofqueso.options.armor_status.on_update"),
    OFF("off", "qualityofqueso.options.armor_status.off");

    public static final Codec<ArmorStatus> CODEC = StringRepresentable.fromEnum(ArmorStatus::values);
    private final String name;
    private final Component translationKey;

    ArmorStatus(final String name, final String translationKey) {
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean off() {
        return this == OFF;
    }

    public boolean onUpdate() {
        return this == ON_UPDATE;
    }

    public boolean always() {
        return this == ALWAYS;
    }

    public @NonNull String getSerializedName() {
        return this.name;
    }

    @Override
    public Component getTranslationKey() {
        return this.translationKey;
    }
}