package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.util.ModTexts;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class ModListOptions {

    public static final SimpleOption<Boolean> CLOSE_GUI_MENU_BY_CLICKING_OFF = new SimpleOption<>("qualityofqueso.options.close_gui_menu_by_clicking_off", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.close_gui_menu_by_clicking_off.tooltip")),
            (optionText, value) -> !value ? ModTexts.NO : ModTexts.YES, SimpleOption.BOOLEAN, ModOptions.OPTIONS.close_gui_menu_by_clicking_off, value -> ModOptions.OPTIONS.close_gui_menu_by_clicking_off = value);

    public static final SimpleOption<Boolean> TYPE_ANYWHERE_TO_SEARCH = new SimpleOption<>("qualityofqueso.options.type_anywhere_to_search", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.type_anywhere_to_search.tooltip")),
            (optionText, value) -> !value ? ModTexts.NO : ModTexts.YES, SimpleOption.BOOLEAN, ModOptions.OPTIONS.type_anywhere_to_search, value -> ModOptions.OPTIONS.type_anywhere_to_search = value);
}