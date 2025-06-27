package net.dillon.qualityofqueso.option;

import net.dillon.qualityofqueso.keybind.ModKeybinds;
import net.dillon.qualityofqueso.main.QualityOfQueso;
import net.dillon.qualityofqueso.util.ModTexts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Options displayed on {@link ModOptionsScreen}.
 */
@Environment(EnvType.CLIENT)
public class ModListOptions {

    public static final SimpleOption<Boolean> BETTER_GUI_EXIT = new SimpleOption<>("qualityofqueso.options.better_gui_exit", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.better_gui_exit.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQueso.options().betterGuiExit, value -> QualityOfQueso.options().betterGuiExit = value);

    public static final SimpleOption<Boolean> BETTER_SEARCHING = new SimpleOption<>("qualityofqueso.options.better_searching", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.better_searching.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQueso.options().betterSearching, value -> QualityOfQueso.options().betterSearching = value);

    public static final SimpleOption<Boolean> CHEST_SEARCH = new SimpleOption<>("qualityofqueso.options.chest_search", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.chest_search.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQueso.options().chestSearch, value -> QualityOfQueso.options().chestSearch = value);

    public static final SimpleOption<Boolean> SEARCH_INVENTORY = new SimpleOption<>("qualityofqueso.options.search_inventory", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.search_inventory.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQueso.options().searchInventory, value -> QualityOfQueso.options().searchInventory = value);

    public static final SimpleOption<Boolean> SAVE_SEARCH_TEXT = new SimpleOption<>("qualityofqueso.options.save_search_text", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.save_search_text.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQueso.options().saveSearchText, value -> QualityOfQueso.options().saveSearchText = value);

    public static final SimpleOption<Boolean> INVENTORY_SORTING = new SimpleOption<>("qualityofqueso.options.inventory_sorting", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.inventory_sorting.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQueso.options().inventorySorting, value -> QualityOfQueso.options().inventorySorting = value);

    public static final SimpleOption<Boolean> REQUIRE_ALT_TO_MOVE = new SimpleOption<>("qualityofqueso.options.require_alt_to_move", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.require_alt_to_move.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQueso.options().requireAltToMove, value -> QualityOfQueso.options().requireAltToMove = value);

    public static final SimpleOption<Boolean> INCLUDE_HOTBAR = new SimpleOption<>("qualityofqueso.options.include_hotbar", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.include_hotbar.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQueso.options().includeHotbar, value -> QualityOfQueso.options().includeHotbar = value);

    public static final SimpleOption<Boolean> QUICK_EQUIP = new SimpleOption<>("qualityofqueso.options.quick_equip", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.quick_equip.tooltip", parseKeyAsString(ModKeybinds.QUICK_EQUIP.boundKey.toString().toUpperCase()))),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQueso.options().quickEquip, value -> QualityOfQueso.options().quickEquip = value);

    public static final SimpleOption<Boolean> ITEM_FRAME_SEARCHING = new SimpleOption<>("qualityofqueso.options.item_frame_searching", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.item_frame_searching.tooltip", parseKeyAsString(ModKeybinds.OPEN_SEARCH_ITEM_FRAMES_GUI.boundKey.toString().toUpperCase()))),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQueso.options().itemFrameSearching, value -> QualityOfQueso.options().itemFrameSearching = value);

    public static final SimpleOption<Boolean> SHOW_CONFIG_BUTTON = new SimpleOption<>("qualityofqueso.options.show_config_button", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.show_config_button.tooltip")),
            (optionText, value) -> !value ? ModTexts.OFF : ModTexts.ON, SimpleOption.BOOLEAN, QualityOfQueso.options().showConfigButton, value -> QualityOfQueso.options().showConfigButton = value);

    public static final SimpleOption<Integer> ITEM_FRAME_SEARCH_TIMER =
            new SimpleOption<>("qualityofqueso.options.item_frame_search_timer", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.item_frame_search_timer.tooltip")),
                    (optionText, value) -> {
                        if (value == 0) {
                            return GameOptions.getGenericValueText(optionText, Text.literal("No Timer").formatted(Formatting.GREEN));
                        } else if (value < 60) {
                            return GameOptions.getGenericValueText(optionText, Text.literal(value + "s"));
                        } else {
                            int minutes = value / 60; // Gets minutes value
                            int seconds = value % 60; // Gets seconds value
                            if (seconds == 0) {
                                return GameOptions.getGenericValueText(optionText, Text.literal(minutes + "m"));
                            } else {
                                return GameOptions.getGenericValueText(optionText, Text.literal(minutes + "m " + seconds + "s"));
                            }
                        }},
                    new SimpleOption.ValidatingIntSliderCallbacks(0, 180), QualityOfQueso.options().itemFrameSearchTimer, value -> QualityOfQueso.options().itemFrameSearchTimer = value);

    public static final SimpleOption<Integer> ITEM_FRAME_SEARCH_RADIUS =
            new SimpleOption<>("qualityofqueso.options.item_frame_search_radius", SimpleOption.constantTooltip(Text.translatable("qualityofqueso.options.item_frame_search_radius.tooltip")),
                    (optionText, value) -> GameOptions.getGenericValueText(optionText, Text.literal(value + "m")),
                    new SimpleOption.ValidatingIntSliderCallbacks(25, 300), QualityOfQueso.options().itemFrameSearchRadius, value -> QualityOfQueso.options().itemFrameSearchRadius = value);

    /**
     * @return the key name (ex. B or F).
     */
    private static String parseKeyAsString(String translationKey) {
        return translationKey.substring(13).toUpperCase();
    }
}