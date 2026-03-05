package net.dillon.qualityofqueso.option;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum MoveItemsIf implements StringRepresentable {
    CONTAINER_ISNT_FILLED(0, "container_isnt_filled", "qualityofqueso.options.move_items_if.container_isnt_filled"),
    LESS_THAN_MAX_STACK_SIZE(1, "less_than_max_item_count", "qualityofqueso.options.move_items_if.less_than_max_item_count"),
    CAN_MOVE_AT_ALL(2, "can_move_at_all", "qualityofqueso.options.move_items_if.can_move_at_all");

    public static final Codec<MoveItemsIf> Codec = StringRepresentable.fromEnum(MoveItemsIf::values);
    private final int ordinal;
    private final String name;
    private final Component translationKey;

    MoveItemsIf(final int ordinal, final String name, final String translationKey) {
        this.ordinal = ordinal;
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean containerIsntFilled() {
        return this == CONTAINER_ISNT_FILLED;
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