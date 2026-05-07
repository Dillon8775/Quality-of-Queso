package net.dillon.qualityofqueso.option.eum.management;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.option.eum.OptionEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum Swapping implements StringRepresentable, OptionEnum {
    BUTTON_OR_KEY("button_or_key", "qualityofqueso.options.button_or_key"),
    KEY_ONLY("key_only", "qualityofqueso.options.key_only"),
    OFF("off", "qualityofqueso.options.management_button.off");

    public static final Codec<Swapping> CODEC = StringRepresentable.fromEnum(Swapping::values);
    private final String name;
    private final Component translationKey;

    Swapping(final String name, final String translationKey) {
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean buttonOrKey() {
        return this == BUTTON_OR_KEY;
    }

    public boolean buttonOrKeyOrKeyOnly() {
        return this == BUTTON_OR_KEY || this == KEY_ONLY;
    }

    public @NonNull String getSerializedName() {
        return this.name;
    }

    @Override
    public Component getTranslationKey() {
        return this.translationKey;
    }
}