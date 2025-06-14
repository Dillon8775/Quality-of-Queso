package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.QualityOfQuesoClient;
import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.util.ModTexts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

/**
 * Options displayed on {@link ModOptionsScreen}.
 */
@Environment(EnvType.CLIENT)
public class ModListOptions {

    public static final SimpleOption<Boolean> BETTER_GUI_EXIT = new SimpleOption<>("qualityofqueso.options.better_gui_exit", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.better_gui_exit.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQuesoClient.options().betterGuiExit, value -> QualityOfQuesoClient.options().betterGuiExit = value);

    public static final SimpleOption<Boolean> BETTER_SEARCHING = new SimpleOption<>("qualityofqueso.options.better_searching", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.better_searching.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQuesoClient.options().betterSearching, value -> QualityOfQuesoClient.options().betterSearching = value);

    public static final SimpleOption<Boolean> CHEST_SEARCH = new SimpleOption<>("qualityofqueso.options.chest_search", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.chest_search.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQuesoClient.options().chestSearch, value -> QualityOfQuesoClient.options().chestSearch = value);

    public static final SimpleOption<Boolean> SEARCH_INVENTORY = new SimpleOption<>("qualityofqueso.options.search_inventory", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.search_inventory.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQuesoClient.options().searchInventory, value -> QualityOfQuesoClient.options().searchInventory = value);

    public static final SimpleOption<Boolean> SAVE_SEARCH_TEXT = new SimpleOption<>("qualityofqueso.options.save_search_text", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.save_search_text.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQuesoClient.options().saveSearchText, value -> QualityOfQuesoClient.options().saveSearchText = value);

    public static final SimpleOption<Boolean> INVENTORY_SORTING = new SimpleOption<>("qualityofqueso.options.inventory_sorting", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.inventory_sorting.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQuesoClient.options().inventorySorting, value -> QualityOfQuesoClient.options().inventorySorting = value);

    public static final SimpleOption<Boolean> REQUIRE_ALT_TO_SORT = new SimpleOption<>("qualityofqueso.options.require_alt_to_sort", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.require_alt_to_sort.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQuesoClient.options().requireAltToSort, value -> QualityOfQuesoClient.options().requireAltToSort = value);

    public static final SimpleOption<Boolean> QUICK_EQUIP = new SimpleOption<>("qualityofqueso.options.quick_equip", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.quick_equip.tooltip", parseKeyAsString(ModKeybinds.QUICK_EQUIP.boundKey.toString().toUpperCase()))),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQuesoClient.options().quickEquip, value -> QualityOfQuesoClient.options().quickEquip = value);

    public static final SimpleOption<Boolean> SHOW_CONFIG_BUTTON = new SimpleOption<>("qualityofqueso.options.show_config_button", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.show_config_button.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQuesoClient.options().showConfigButton, value -> QualityOfQuesoClient.options().showConfigButton = value);

    /**
     * @return the key name (ex. B or F).
     */
    private static String parseKeyAsString(String translationKey) {
        return translationKey.substring(13).toUpperCase();
    }
}