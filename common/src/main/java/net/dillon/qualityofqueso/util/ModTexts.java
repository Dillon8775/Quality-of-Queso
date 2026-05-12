package net.dillon.qualityofqueso.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/**
 * Texts for {@code Quality of Queso.}
 */
public class ModTexts {
    public static final Component BLANK = Component.literal("");
    public static final Component YES = Component.translatable("qualityofqueso.gui.yes").withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.BOLD);
    public static final Component NO = Component.translatable("qualityofqueso.gui.no").withStyle(ChatFormatting.GRAY);
    public static final Component ON = Component.translatable("qualityofqueso.gui.on").withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.BOLD);
    public static final Component OFF = Component.translatable("qualityofqueso.gui.off").withStyle(ChatFormatting.GRAY);

    public static final int TEXT_COLOR = -12566464;
    public static final int TAG_COLOR = 0x7FFFFF;
    public static final int ITEM_COLOR = 0x96FFB7;
}