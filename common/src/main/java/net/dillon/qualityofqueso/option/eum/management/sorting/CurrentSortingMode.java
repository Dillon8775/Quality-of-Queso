package net.dillon.qualityofqueso.option.eum.management.sorting;

import net.dillon.qualityofqueso.helper.ModConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

public enum CurrentSortingMode {
    ALPHABETICAL("", Component.literal("alphabetically").withColor(ModConstants.ITEM_COLOR)),
    TAG("_tag", Component.literal("by ").append(Component.literal("tag").withColor(ModConstants.TAG_COLOR))),
    COUNT_DESCENDING("_descending", Component.literal("descending").withColor(CommonColors.SOFT_RED)),
    COUNT_ASCENDING("_ascending", Component.literal("ascending").withColor(CommonColors.GREEN)),
    CREATIVE_MENU("_creative_menu", Component.literal("by ").append(Component.literal("§ac§br§5e§6a§7t§di§ev§9e§r menu")));

    private final String appended;
    private final Component tooltip;

    CurrentSortingMode(String appended, Component tooltip) {
        this.appended = appended;
        this.tooltip = tooltip;
    }

    public boolean tag() {
        return this == TAG;
    }

    public boolean count() {
        return this == COUNT_DESCENDING || this == COUNT_ASCENDING;
    }

    public String getAppended() {
        return this.appended;
    }

    public Component getTooltip() {
        return this.tooltip;
    }

    public CurrentSortingMode next(boolean forward) {
        CurrentSortingMode[] values = values();
        int index = (ordinal() + (forward ? 1 : -1) + values.length) % values.length;
        return values[index];
    }

    public static CurrentSortingMode fromName(String name) {
        if (name == null || name.isBlank()) {
            return ALPHABETICAL;
        }

        for (CurrentSortingMode mode : values()) {
            if (mode.name().equalsIgnoreCase(name)) {
                return mode;
            }
        }

        return ALPHABETICAL;
    }
}