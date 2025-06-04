package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.QualityOfQuesoClient;
import net.dillon.qualityofqueso.util.ModTexts;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class ModListOptions {

    public static final SimpleOption<Boolean> CLOSE_GUI_BY_CLICKING_OFF = new SimpleOption<>("qualityofqueso.options.close_gui_by_clicking_off", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.close_gui_by_clicking_off.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQuesoClient.options().closeGuiByClickingOff, value -> QualityOfQuesoClient.options().closeGuiByClickingOff = value);

    public static final SimpleOption<Boolean> TYPE_ANYWHERE_TO_SEARCH = new SimpleOption<>("qualityofqueso.options.type_anywhere_to_search", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.type_anywhere_to_search.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQuesoClient.options().typeAnywhereToSearch, value -> QualityOfQuesoClient.options().typeAnywhereToSearch = value);

    public static final SimpleOption<Boolean> SHOW_CONFIG_BUTTON = new SimpleOption<>("qualityofqueso.options.show_config_button", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.show_config_button.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQuesoClient.options().showConfigurationButton, value -> QualityOfQuesoClient.options().showConfigurationButton = value);
}