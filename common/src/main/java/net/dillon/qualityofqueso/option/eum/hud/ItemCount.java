package net.dillon.qualityofqueso.option.eum.hud;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum ItemCount implements StringRepresentable {
    TOTAL(0, "always", "qualityofqueso.options.item_count.total"),
    STACKS(1, "stacks", "qualityofqueso.options.item_count.stacks"),
    OFF(1, "off", "qualityofqueso.options.item_count.off");

    public static final Codec<ItemCount> Codec = StringRepresentable.fromEnum(ItemCount::values);
    private final int ordinal;
    private final String name;
    private final Component translationKey;

    ItemCount(final int ordinal, final String name, final String translationKey) {
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