package net.dillon.qualityofqueso.option.eum.management;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum ButtonSounds implements StringRepresentable {
    ALL(0, "all", "qualityofqueso.options.button_sounds.all"),
    BUNDLE_ONLY(1, "bundle_only", "qualityofqueso.options.button_sounds.bundle_only"),
    CLICK_ONLY(2, "click_only", "qualityofqueso.options.button_sounds.click_only"),
    OFF(3, "off", "qualityofqueso.options.button_sounds.off");

    public static final Codec<ButtonSounds> Codec = StringRepresentable.fromEnum(ButtonSounds::values);
    private final int ordinal;
    private final String name;
    private final Component translationKey;

    ButtonSounds(final int ordinal, final String name, final String translationKey) {
        this.ordinal = ordinal;
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean off() {
        return this == OFF;
    }

    public boolean bundleOnly() {
        return this == BUNDLE_ONLY;
    }

    public boolean clickOnly() {
        return this == CLICK_ONLY;
    }

    public int getId() {
        return this.ordinal;
    }

    public String getSerializedName() {
        return this.name;
    }

    public Component getText() {
        return this.translationKey;
    }
}