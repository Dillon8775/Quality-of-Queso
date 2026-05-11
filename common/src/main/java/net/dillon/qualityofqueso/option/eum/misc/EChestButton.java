package net.dillon.qualityofqueso.option.eum.misc;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.option.eum.OptionEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum EChestButton implements StringRepresentable, OptionEnum {
    QOQ_MENU("qoq_menu", "qualityofqueso.options.echest_button.qoq_menu"),
    PAUSE_SCREEN("pause_screen", "qualityofqueso.options.echest_button.pause_screen"),
    OFF("off", "qualityofqueso.options.echest_button.off");

    public static final Codec<EChestButton> CODEC = StringRepresentable.fromEnum(EChestButton::values);
    private final String name;
    private final Component translationKey;

    EChestButton(final String name, final String translationKey) {
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean qoqMenu() {
        return this == QOQ_MENU;
    }

    public boolean pauseScreen() {
        return this == PAUSE_SCREEN;
    }

    public boolean enabled() {
        return this != OFF;
    }

    @Override
    public @NonNull String getSerializedName() {
        return this.name;
    }

    @Override
    public Component getTranslationKey() {
        return this.translationKey;
    }
}