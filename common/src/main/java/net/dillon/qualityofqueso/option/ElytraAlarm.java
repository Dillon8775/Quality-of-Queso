package net.dillon.qualityofqueso.option;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum ElytraAlarm implements StringRepresentable {
    ON(0, "on", "qualityofqueso.options.elytra_alarm.on"),
    INDICATOR_ONLY(1, "indicator_only", "qualityofqueso.options.elytra_alarm.indicator_only"),
    OFF(2, "off", "qualityofqueso.options.elytra_alarm.off");

    public static final Codec<ElytraAlarm> Codec = StringRepresentable.fromEnum(ElytraAlarm::values);
    private final int ordinal;
    private final String name;
    private final Component translationKey;

    ElytraAlarm(final int ordinal, final String name, final String translationKey) {
        this.ordinal = ordinal;
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean enabled() {
        return this != OFF;
    }

    public boolean indicatorOnly() {
        return this == INDICATOR_ONLY;
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