package net.dillon.qualityofqueso.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/**
 * Texts for {@code Quality of Queso.}
 */
public class ModTexts {
    public static final Component CONFIGURE_QOQ = Component.translatable("qualityofqueso.gui.options.tooltip");
    public static final Component BLANK = Component.literal("");
    public static final Component ON = Component.translatable("qualityofqueso.gui.on").withStyle(ChatFormatting.YELLOW);
    public static final Component OFF = Component.translatable("qualityofqueso.gui.off").withStyle(ChatFormatting.GOLD);
}