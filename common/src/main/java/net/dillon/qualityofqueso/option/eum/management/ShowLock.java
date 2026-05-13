package net.dillon.qualityofqueso.option.eum.management;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.option.eum.OptionEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum ShowLock implements StringRepresentable, OptionEnum {
    EVERYWHERE("button_or_key", "qualityofqueso.options.show_lock.everywhere"),
    GUI_ONLY("gui_only", "qualityofqueso.options.show_lock.gui_only"),
    SCREEN_ONLY("screen_only", "qualityofqueso.options.show_lock.screen_only"),
    OFF("off", "qualityofqueso.options.show_lock.off");

    public static final Codec<ShowLock> CODEC = StringRepresentable.fromEnum(ShowLock::values);
    private final String name;
    private final Component translationKey;

    public boolean inScreens() {
        return this == EVERYWHERE || this == SCREEN_ONLY;
    }

    public boolean inGui() {
        return this == EVERYWHERE || this == GUI_ONLY;
    }

    ShowLock(final String name, final String translationKey) {
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public @NonNull String getSerializedName() {
        return this.name;
    }

    @Override
    public Component getTranslationKey() {
        return this.translationKey;
    }
}