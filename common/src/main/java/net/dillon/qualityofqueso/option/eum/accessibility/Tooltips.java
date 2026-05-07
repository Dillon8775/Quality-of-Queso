package net.dillon.qualityofqueso.option.eum.accessibility;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.option.eum.OptionEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum Tooltips implements StringRepresentable, OptionEnum {
    DEFAULT("default", "qualityofqueso.options.tooltips.default"),
    OVERLAY("overlay", "qualityofqueso.options.tooltips.overlay"),
    OFF("off", "qualityofqueso.options.tooltips.off");

    public static final Codec<Tooltips> CODEC = StringRepresentable.fromEnum(Tooltips::values);
    private final String name;
    private final Component translationKey;

    Tooltips(final String name, final String translationKey) {
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean on() {
        return this != OFF;
    }

    public boolean ddefault() {
        return this == DEFAULT;
    }

    public @NonNull String getSerializedName() {
        return this.name;
    }

    @Override
    public Component getTranslationKey() {
        return this.translationKey;
    }
}