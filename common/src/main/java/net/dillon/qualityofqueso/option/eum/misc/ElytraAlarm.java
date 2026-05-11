package net.dillon.qualityofqueso.option.eum.misc;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.option.eum.OptionEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum ElytraAlarm implements StringRepresentable, OptionEnum {
    ON("on", "qualityofqueso.options.elytra_alarm.on"),
    INDICATOR_ONLY("indicator_only", "qualityofqueso.options.elytra_alarm.indicator_only"),
    OFF("off", "qualityofqueso.options.elytra_alarm.off");

    public static final Codec<ElytraAlarm> CODEC = StringRepresentable.fromEnum(ElytraAlarm::values);
    private final String name;
    private final Component translationKey;

    ElytraAlarm(final String name, final String translationKey) {
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean enabled() {
        return this != OFF;
    }

    public boolean indicatorOnly() {
        return this == INDICATOR_ONLY;
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