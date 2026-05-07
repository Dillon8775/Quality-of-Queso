package net.dillon.qualityofqueso.option.eum.effects;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.option.eum.OptionEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum Bows implements StringRepresentable, OptionEnum {
    DEFAULT("default", "qualityofqueso.options.fov_effects.bows.default"),
    QUICK_PULL("quick_pull", "qualityofqueso.options.fov_effects.bows.quick_pull"),
    OFF("off", "qualityofqueso.options.fov_effects.bows.off");

    public static final Codec<Bows> CODEC = StringRepresentable.fromEnum(Bows::values);
    private final String name;
    private final Component translationKey;

    Bows(final String name, final String translationKey) {
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean enabled() {
        return this == DEFAULT || this == QUICK_PULL;
    }

    public boolean ddefault() {
        return this == DEFAULT;
    }

    public boolean quickPull() {
        return this == QUICK_PULL;
    }

    public @NonNull String getSerializedName() {
        return this.name;
    }

    @Override
    public Component getTranslationKey() {
        return this.translationKey;
    }
}