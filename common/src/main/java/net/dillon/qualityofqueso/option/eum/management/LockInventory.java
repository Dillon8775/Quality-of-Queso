package net.dillon.qualityofqueso.option.eum.management;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum LockInventory implements StringRepresentable {
    UNLOCKED("unlocked"),
    SOFT_LOCKED("soft_locked"),
    LOCKED("locked");

    public static final Codec<LockInventory> CODEC = StringRepresentable.fromEnum(LockInventory::values);
    private final String name;

    LockInventory(final String name) {
        this.name = name;
    }

    public boolean inventoryLocked() {
        return this != UNLOCKED;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}