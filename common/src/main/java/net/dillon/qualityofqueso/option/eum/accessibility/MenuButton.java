package net.dillon.qualityofqueso.option.eum.accessibility;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.option.eum.OptionEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

/**
 * Defines where the Quality of Queso menu button should appear.
 */
public enum MenuButton implements StringRepresentable, OptionEnum {
    EVERYWHERE("everywhere", "qualityofqueso.options.menu_button.everywhere"),
    BOTTOM_LEFT("bottom", "qualityofqueso.options.menu_button.bottom_left"),
    BOTTOM_RIGHT("bottom", "qualityofqueso.options.menu_button.bottom_right"),
    TITLE_ONLY("title_only", "qualityofqueso.options.menu_button.title_only"),
    OFF("off", "qualityofqueso.options.menu_button.off");

    public static final Codec<MenuButton> CODEC = StringRepresentable.fromEnum(MenuButton::values);
    private final String name;
    private final Component translationKey;

    MenuButton(final String name, final String translationKey) {
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean enabled() {
        return this != OFF;
    }

    public boolean left() {
        return this == BOTTOM_LEFT;
    }

    public boolean right() {
        return this == BOTTOM_RIGHT;
    }

    public boolean everywhere() {
        return this != OFF && this != TITLE_ONLY;
    }

    public @NonNull String getSerializedName() {
        return this.name;
    }

    @Override
    public Component getTranslationKey() {
        return this.translationKey;
    }
}