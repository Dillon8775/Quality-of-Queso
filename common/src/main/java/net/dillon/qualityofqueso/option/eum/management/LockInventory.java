package net.dillon.qualityofqueso.option.eum.management;

import com.mojang.serialization.Codec;
import net.dillon.qualityofqueso.option.eum.OptionEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum LockInventory implements StringRepresentable, OptionEnum {
    UNLOCKED("unlocked", "qualityofqueso.options.lock_inventory.unlocked"),
    SOFT_LOCKED("soft_locked", "qualityofqueso.options.lock_inventory.soft_locked"),
    LOCKED("locked", "qualityofqueso.options.lock_inventory.locked");

    public static final Codec<LockInventory> CODEC = StringRepresentable.fromEnum(LockInventory::values);
    private final String name;
    private final Component translationKey;

    LockInventory(final String name, final String translationKey) {
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean inventoryLocked() {
        return this != UNLOCKED;
    }

    public String getSerializedName() {
        return this.name;
    }

    @Override
    public Component getTranslationKey() {
        return this.translationKey;
    }
}