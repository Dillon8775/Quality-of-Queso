package net.dillon.qualityofqueso;

import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class QuesoListOptions {

    public static final SimpleOption<Boolean> CLOSE_GUI_MENU_BY_CLICKING_OFF = new SimpleOption<>("qualityofqueso.options.close_gui_menu_by_clicking_off", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.close_gui_menu_by_clicking_off.tooltip")),
            (optionText, value) -> !value ? QuesoTexts.NO : QuesoTexts.YES, SimpleOption.BOOLEAN, QuesoOptions.OPTIONS.close_gui_menu_by_clicking_off, value -> QuesoOptions.OPTIONS.close_gui_menu_by_clicking_off = value);

    public static final SimpleOption<Boolean> TYPE_ANYWHERE_TO_SEARCH = new SimpleOption<>("qualityofqueso.options.type_anywhere_to_search", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.type_anywhere_to_search.tooltip")),
            (optionText, value) -> !value ? QuesoTexts.NO : QuesoTexts.YES, SimpleOption.BOOLEAN, QuesoOptions.OPTIONS.type_anywhere_to_search, value -> QuesoOptions.OPTIONS.type_anywhere_to_search = value);
}