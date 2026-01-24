package net.dillon.qualityofqueso.option;

import com.mojang.serialization.Codec;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public enum ItemCount implements StringIdentifiable {
    TOTAL(0, "always", "qualityofqueso.options.item_count.total"),
    STACKS(1, "on_change", "qualityofqueso.options.item_count.stacks"),
    REMAINDER(2, "off", "qualityofqueso.options.item_count.remainder"),
    OFF(3, "off", "qualityofqueso.options.item_count.off");

    public static final Codec<ItemCount> Codec = StringIdentifiable.createCodec(ItemCount::values);
    private final int ordinal;
    private final String name;
    private final Text translationKey;

    ItemCount(final int ordinal, final String name, final String translationKey) {
        this.ordinal = ordinal;
        this.name = name;
        this.translationKey = Text.translatable(translationKey);
    }

    public boolean enabled() {
        return this != OFF;
    }

    public int getId() {
        return this.ordinal;
    }

    public Text getText() {
        return this.translationKey;
    }

    public String asString() {
        return this.name;
    }
}