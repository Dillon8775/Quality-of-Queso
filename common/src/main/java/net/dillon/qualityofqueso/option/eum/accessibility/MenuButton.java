package net.dillon.qualityofqueso.option.eum.accessibility;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

/**
 * Defines where the Quality of Queso menu button should appear.
 */
public enum MenuButton implements StringRepresentable {
    EVERYWHERE(0, "everywhere", "qualityofqueso.options.menu_button.everywhere"),
    BOTTOM_LEFT(1, "bottom", "qualityofqueso.options.menu_button.bottom_left"),
    BOTTOM_RIGHT(1, "bottom", "qualityofqueso.options.menu_button.bottom_right"),
    TITLE_ONLY(2, "title_only", "qualityofqueso.options.menu_button.title_only"),
    OFF(3, "off", "qualityofqueso.options.menu_button.off");

    public static final Codec<MenuButton> Codec = StringRepresentable.fromEnum(MenuButton::values);
    private final int ordinal;
    private final String name;
    private final Component translationKey;

    MenuButton(final int ordinal, final String name, final String translationKey) {
        this.ordinal = ordinal;
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