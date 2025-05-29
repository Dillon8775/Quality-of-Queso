package net.dillon.qualityofqueso;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class QuesoTexts {
    public static final Text BLANK = Text.literal("");
    public static final Text ON = Text.translatable("qualityofqueso.gui.on").formatted(Formatting.GREEN);
    public static final Text OFF = Text.translatable("qualityofqueso.gui.off").formatted(Formatting.RED);
    public static final Text YES = Text.translatable("qualityofqueso.gui.yes").formatted(Formatting.GREEN);
    public static final Text NO = Text.translatable("qualityofqueso.gui.no").formatted(Formatting.RED);
}