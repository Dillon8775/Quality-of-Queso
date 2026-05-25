package net.dillon.qualityofqueso.option.eum.misc;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.util.StringRepresentable;

public enum ViewLastKnownEnderChestButton implements StringRepresentable {
    QOQ_MENU("§6QOQ Main Menu"),
    PAUSE_SCREEN("§bPause Screen"),
    OFF(ModConstants.OFF_SERIALIZED_NAME);

    public static final Codec<ViewLastKnownEnderChestButton> CODEC = StringRepresentable.fromEnum(ViewLastKnownEnderChestButton::values);
    private final String name;

    ViewLastKnownEnderChestButton(final String name) {
        this.name = name;
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
    public String getSerializedName() {
        return this.name;
    }
}