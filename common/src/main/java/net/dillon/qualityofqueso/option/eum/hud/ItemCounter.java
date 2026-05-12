package net.dillon.qualityofqueso.option.eum.hud;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum ItemCounter implements StringRepresentable {
    TOTAL(0, "always", "qualityofqueso.options.item_counter.total"),
    STACKS(1, "stacks", "qualityofqueso.options.item_counter.stacks"),
    OFF(1, "off", "qualityofqueso.options.item_counter.off");

    public static final Codec<ItemCounter> Codec = StringRepresentable.fromEnum(ItemCounter::values);
    private final int ordinal;
    private final String name;
    private final Component translationKey;

    ItemCounter(final int ordinal, final String name, final String translationKey) {
        this.ordinal = ordinal;
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean enabled() {
        return this != OFF;
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