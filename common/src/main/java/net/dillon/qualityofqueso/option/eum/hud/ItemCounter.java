package net.dillon.qualityofqueso.option.eum.hud;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.helper.ModConstants;
import net.minecraft.util.StringRepresentable;

public enum ItemCounter implements StringRepresentable {
    STACKS("§bStacks"),
    TOTAL("§aTotal"),
    OFF(ModConstants.OFF_SERIALIZED_NAME);

    public static final Codec<ItemCounter> CODEC = StringRepresentable.fromEnum(ItemCounter::values);
    private final String name;

    ItemCounter(final String name) {
        this.name = name;
    }

    public boolean enabled() {
        return this != OFF;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}