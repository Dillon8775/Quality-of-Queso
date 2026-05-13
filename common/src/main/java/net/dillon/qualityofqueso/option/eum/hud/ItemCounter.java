package net.dillon.qualityofqueso.option.eum.hud;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.option.eum.OptionEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum ItemCounter implements StringRepresentable, OptionEnum {
    TOTAL("always", "qualityofqueso.options.item_counter.total"),
    STACKS("stacks", "qualityofqueso.options.item_counter.stacks"),
    OFF("off", "qualityofqueso.options.item_counter.off");

    public static final Codec<ItemCounter> CODEC = StringRepresentable.fromEnum(ItemCounter::values);
    private final String name;
    private final Component translationKey;

    ItemCounter(final String name, final String translationKey) {
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean enabled() {
        return this != OFF;
    }

    public String getSerializedName() {
        return this.name;
    }

    @Override
    public Component getTranslationKey() {
        return this.translationKey;
    }
}