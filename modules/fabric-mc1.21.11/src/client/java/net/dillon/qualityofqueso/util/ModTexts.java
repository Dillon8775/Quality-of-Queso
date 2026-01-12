package net.dillon.qualityofqueso.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Texts for {@code Quality of Queso.}
 */
@Environment(EnvType.CLIENT)
public class ModTexts {
    public static final Text BLANK = Text.literal("");
    public static final Text YES = Text.translatable("qualityofqueso.gui.yes").formatted(Formatting.GREEN).formatted(Formatting.BOLD);
    public static final Text NO = Text.translatable("qualityofqueso.gui.no").formatted(Formatting.RED);
    public static final Text ON = Text.translatable("qualityofqueso.gui.on").formatted(Formatting.YELLOW).formatted(Formatting.BOLD);
    public static final Text OFF = Text.translatable("qualityofqueso.gui.off").formatted(Formatting.GOLD);
}