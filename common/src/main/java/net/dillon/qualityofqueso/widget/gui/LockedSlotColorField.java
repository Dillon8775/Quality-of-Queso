package net.dillon.qualityofqueso.widget.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

/**
 * The field for changing the color of locked slots.
 */
public class LockedSlotColorField extends ColorField {

    public LockedSlotColorField(Font font, int textColor, int defaultTextColor, Component validTooltip) {
        super(font, textColor, defaultTextColor, validTooltip);
    }

    @Override
    public int getSafeColor() {
        return -6250241;
    }
}