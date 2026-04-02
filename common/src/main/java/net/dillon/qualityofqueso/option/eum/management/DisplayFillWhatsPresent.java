package net.dillon.qualityofqueso.option.eum.management;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum DisplayFillWhatsPresent implements StringRepresentable {
    ALWAYS(0, "always", "qualityofqueso.options.display_fill_whats_present.always"),
    FILTERED_CONTAINERS_ONLY(1, "filtered_containers_only", "qualityofqueso.options.display_fill_whats_present.filtered_containers_only"),
    ;

    public static final Codec<DisplayFillWhatsPresent> Codec = StringRepresentable.fromEnum(DisplayFillWhatsPresent::values);
    private final int ordinal;
    private final String name;
    private final Component translationKey;

    DisplayFillWhatsPresent(final int ordinal, final String name, final String translationKey) {
        this.ordinal = ordinal;
        this.name = name;
        this.translationKey = Component.translatable(translationKey);
    }

    public boolean filteredContainersOnly() {
        return this == FILTERED_CONTAINERS_ONLY;
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