package net.dillon.qualityofqueso.widget.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

import static net.dillon.qualityofqueso.util.ModConstants.HEX_COLOR_PICKER;

/**
 * A color field, for holding and translating colors.
 */
public class ColorField extends EditBox {
    private final int defaultTextColor;
    private final Component validTooltip;
    private Component currentTooltip;
    private boolean safeToSaveColor;

    public ColorField(Font font, int textColor, int defaultTextColor, Component validTooltip) {
        super(font, 0, 0, 150, 20, Component.empty());
        this.defaultTextColor = defaultTextColor;
        this.validTooltip = validTooltip;
        this.safeToSaveColor = true;
        this.setMaxLength(9);
        this.setValue(formatColorHex(textColor));
        this.setTextColor(this.getSafeColor());
        this.setResponder(this::onColorChanged);
    }

    /**
     * @return the default text color for the field.
     */
    public int getDefaultTextColor() {
        return this.defaultTextColor;
    }

    /**
     * @return the color field's current tooltip.
     */
    public Component getCurrentTooltip() {
        return this.currentTooltip == null ? this.validTooltip : this.currentTooltip;
    }

    /**
     * @return if the text color is safe to save.
     */
    public boolean isSafeToSaveColor() {
        return this.safeToSaveColor;
    }

    /**
     * @return the safe color to display.
     */
    public int getSafeColor() {
        return CommonColors.GREEN;
    }

    /**
     * Gets the text color in ARGB format.
     */
    public static int getTextColor(String colorHex) {
        String normalized = parseTextColor(colorHex);
        return normalized.length() == 6
                ? 0xFF000000 | Integer.parseInt(normalized, 16)
                : (int) Long.parseLong(normalized, 16);
    }

    /**
     * Normalizes the color hex.
     */
    private static String parseTextColor(String color) {
        String normalized = color.startsWith("#") ? color.substring(1) : color;
        if (!normalized.matches("[0-9a-fA-F]{6}|[0-9a-fA-F]{8}")) {
            throw new NumberFormatException("Hex digits must be exactly 6 (RGB) or 8 (ARGB) characters.");
        }
        return normalized;
    }

    /**
     * Formats the color hex.
     */
    private static String formatColorHex(int color) {
        int alpha = color >>> 24;
        return alpha == 0xFF
                ? String.format("#%06X", color & 0xFFFFFF)
                : String.format("#%08X", color);
    }

    /**
     * Attempts to change the color of the search bar text field.
     */
    private void onColorChanged(String newColor) {
        try {
            parseTextColor(newColor);
            this.setTextColor(this.getSafeColor());
            this.safeToSaveColor = true;
            this.currentTooltip = this.validTooltip;
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            this.setTextColor(CommonColors.RED);
            this.safeToSaveColor = false;
            this.currentTooltip = Component.translatable("qualityofqueso.options.color_field.error").withStyle(ChatFormatting.RED);
        }
    }

    /**
     * Handles clicking on the text color field.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int bl) {
        if (this.isMouseOver(mouseX, mouseY)) {
            if (Screen.hasShiftDown()) {
                if (bl == 0) {
                    Util.getPlatform().openUri(HEX_COLOR_PICKER);
                    return true;
                } else if (bl == 1) {
                    this.setValue(formatColorHex(this.defaultTextColor));
                    this.setFocused(false);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, bl);
    }
}
