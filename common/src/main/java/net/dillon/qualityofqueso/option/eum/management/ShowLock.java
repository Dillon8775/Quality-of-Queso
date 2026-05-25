package net.dillon.qualityofqueso.option.eum.management;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.util.ModConstants;
import net.minecraft.util.StringRepresentable;

public enum ShowLock implements StringRepresentable {
    EVERYWHERE("§aEverywhere"),
    HUD_ONLY("§bHUD Only"),
    SCREEN_ONLY("§6Screens Only"),
    OFF(ModConstants.OFF_SERIALIZED_NAME);

    public static final Codec<ShowLock> CODEC = StringRepresentable.fromEnum(ShowLock::values);
    private final String name;

    ShowLock(final String name) {
        this.name = name;
    }

    public boolean inScreens() {
        return this == EVERYWHERE || this == SCREEN_ONLY;
    }

    public boolean inHud() {
        return this == EVERYWHERE || this == HUD_ONLY;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}